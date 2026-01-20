package com.yourcompany.pos.sales;

import com.yourcompany.pos.auth.AuditLog;
import com.yourcompany.pos.auth.AuditLogRepository;
import com.yourcompany.pos.auth.CashRegisterShift;
import com.yourcompany.pos.auth.CashRegisterShiftRepository;
import com.yourcompany.pos.auth.DocumentNumberService;
import com.yourcompany.pos.common.InventoryTxnType;
import com.yourcompany.pos.common.InvoiceStatus;
import com.yourcompany.pos.common.ShiftStatus;
import com.yourcompany.pos.inventory.InventoryMovement;
import com.yourcompany.pos.inventory.InventoryMovementRepository;
import com.yourcompany.pos.master.Product;
import com.yourcompany.pos.master.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesInvoiceRepository salesInvoiceRepository;
    private final SalesItemRepository salesItemRepository;
    private final SalesPaymentRepository salesPaymentRepository;
    private final CashRegisterShiftRepository shiftRepository;
    private final DocumentNumberService documentNumberService;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final AuditLogRepository auditLogRepository;
    private final SalesCalculator calculator = new SalesCalculator();

    @Transactional
    public SalesResponse createSale(SalesRequest request) {
        CashRegisterShift shift = shiftRepository.findByTerminalCodeAndStatus(request.getTerminalCode(), ShiftStatus.OPEN)
            .orElseThrow(() -> new IllegalArgumentException("No active shift for terminal"));
        String invoiceNo = documentNumberService.nextSalesInvoice(LocalDate.now());

        List<SalesCalculator.LineInput> inputs = request.getItems().stream()
            .map(item -> new SalesCalculator.LineInput(getProduct(item.getProductId()), item.getQty(), item.getDiscountAmount()))
            .collect(Collectors.toList());
        SalesCalculator.CalculationResult result = calculator.calculate(inputs, request.getBillDiscountAmount());

        SalesInvoice invoice = new SalesInvoice();
        invoice.setInvoiceNo(invoiceNo);
        invoice.setTerminalCode(request.getTerminalCode());
        invoice.setShift(shift);
        invoice.setStatus(InvoiceStatus.COMPLETED);
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerPhone(request.getCustomerPhone());
        applyTotals(invoice, result);
        salesInvoiceRepository.save(invoice);

        persistItemsAndMovements(invoice, result);
        persistPayments(invoice, request.getPayments());
        logAudit("SALE_CREATE", "Sale created " + invoiceNo);

        return new SalesResponse(invoice.getId().toString(), invoiceNo, invoice.getGrandTotal(), request.getPayments(), null);
    }

    @Transactional
    public SalesResponse holdSale(SalesHoldRequest request) {
        String invoiceNo = documentNumberService.nextSalesInvoice(LocalDate.now());
        List<SalesCalculator.LineInput> inputs = request.getItems().stream()
            .map(item -> new SalesCalculator.LineInput(getProduct(item.getProductId()), item.getQty(), item.getDiscountAmount()))
            .collect(Collectors.toList());
        SalesCalculator.CalculationResult result = calculator.calculate(inputs, request.getBillDiscountAmount());

        SalesInvoice invoice = new SalesInvoice();
        invoice.setInvoiceNo(invoiceNo);
        invoice.setTerminalCode(request.getTerminalCode());
        invoice.setStatus(InvoiceStatus.HELD);
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerPhone(request.getCustomerPhone());
        applyTotals(invoice, result);
        salesInvoiceRepository.save(invoice);

        persistItems(invoice, result);
        logAudit("SALE_HOLD", "Sale held " + invoiceNo);
        return new SalesResponse(invoice.getId().toString(), invoiceNo, invoice.getGrandTotal(), List.of(), null);
    }

    public List<SalesInvoice> heldSales(String terminalCode) {
        return salesInvoiceRepository.findByStatusAndTerminalCode(InvoiceStatus.HELD, terminalCode);
    }

    public SalesDetailResponse getSalesDetail(UUID salesId) {
        SalesInvoice invoice = salesInvoiceRepository.findById(salesId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return new SalesDetailResponse(invoice,
            salesItemRepository.findBySalesInvoiceId(salesId),
            salesPaymentRepository.findBySalesInvoiceId(salesId));
    }

    public SalesDetailResponse getByInvoiceNo(String invoiceNo) {
        SalesInvoice invoice = salesInvoiceRepository.findByInvoiceNo(invoiceNo)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return getSalesDetail(invoice.getId());
    }

    @Transactional
    public SalesResponse completeHeld(UUID salesId, CompleteHeldRequest request) {
        SalesInvoice invoice = salesInvoiceRepository.findById(salesId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (invoice.getStatus() != InvoiceStatus.HELD) {
            throw new IllegalArgumentException("Invoice is not held");
        }
        CashRegisterShift shift = shiftRepository.findByTerminalCodeAndStatus(invoice.getTerminalCode(), ShiftStatus.OPEN)
            .orElseThrow(() -> new IllegalArgumentException("No active shift for terminal"));
        invoice.setShift(shift);
        invoice.setStatus(InvoiceStatus.COMPLETED);
        salesInvoiceRepository.save(invoice);

        List<SalesItem> items = salesItemRepository.findBySalesInvoiceId(invoice.getId());
        for (SalesItem item : items) {
            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(item.getProduct());
            movement.setTxnType(InventoryTxnType.SALE_OUT);
            movement.setQtyOut(item.getQty());
            movement.setRefNo(invoice.getInvoiceNo());
            inventoryMovementRepository.save(movement);
        }
        persistPayments(invoice, request.getPayments());
        logAudit("SALE_COMPLETE", "Sale completed " + invoice.getInvoiceNo());
        return new SalesResponse(invoice.getId().toString(), invoice.getInvoiceNo(), invoice.getGrandTotal(), request.getPayments(), null);
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private void applyTotals(SalesInvoice invoice, SalesCalculator.CalculationResult result) {
        invoice.setSubtotal(result.getSubtotal());
        invoice.setDiscountTotal(result.getDiscountTotal());
        invoice.setTaxableTotal(result.getTaxableTotal());
        invoice.setCgstTotal(result.getCgstTotal());
        invoice.setSgstTotal(result.getSgstTotal());
        invoice.setRoundOff(result.getRoundOff());
        invoice.setGrandTotal(result.getGrandTotal());
    }

    private void persistItemsAndMovements(SalesInvoice invoice, SalesCalculator.CalculationResult result) {
        for (SalesCalculator.LineResult line : result.getLineResults()) {
            SalesItem item = toSalesItem(invoice, line);
            salesItemRepository.save(item);
            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(line.getProduct());
            movement.setTxnType(InventoryTxnType.SALE_OUT);
            movement.setQtyOut(line.getQty());
            movement.setRefNo(invoice.getInvoiceNo());
            inventoryMovementRepository.save(movement);
        }
    }

    private void persistItems(SalesInvoice invoice, SalesCalculator.CalculationResult result) {
        for (SalesCalculator.LineResult line : result.getLineResults()) {
            SalesItem item = toSalesItem(invoice, line);
            salesItemRepository.save(item);
        }
    }

    private SalesItem toSalesItem(SalesInvoice invoice, SalesCalculator.LineResult line) {
        SalesItem item = new SalesItem();
        item.setSalesInvoice(invoice);
        item.setProduct(line.getProduct());
        item.setQty(line.getQty());
        item.setPrice(line.getPrice());
        item.setDiscountAmount(line.getDiscountAmount());
        item.setTaxableAmount(line.getTaxableAmount());
        item.setCgstAmount(line.getCgstAmount());
        item.setSgstAmount(line.getSgstAmount());
        item.setLineTotal(line.getLineTotal());
        return item;
    }

    private void persistPayments(SalesInvoice invoice, List<SalesPaymentRequest> payments) {
        for (SalesPaymentRequest req : payments) {
            SalesPayment payment = new SalesPayment();
            payment.setSalesInvoice(invoice);
            payment.setMode(req.getMode());
            payment.setAmount(req.getAmount());
            payment.setRefNo(req.getRefNo());
            salesPaymentRepository.save(payment);
        }
    }

    private void logAudit(String type, String message) {
        AuditLog log = new AuditLog();
        log.setEventType(type);
        log.setEventMessage(message);
        auditLogRepository.save(log);
    }
}
