package com.yourcompany.pos.returns;

import com.yourcompany.pos.auth.AuditLog;
import com.yourcompany.pos.auth.AuditLogRepository;
import com.yourcompany.pos.auth.DocumentNumberService;
import com.yourcompany.pos.common.InventoryTxnType;
import com.yourcompany.pos.inventory.InventoryMovement;
import com.yourcompany.pos.inventory.InventoryMovementRepository;
import com.yourcompany.pos.sales.SalesInvoice;
import com.yourcompany.pos.sales.SalesInvoiceRepository;
import com.yourcompany.pos.sales.SalesItem;
import com.yourcompany.pos.sales.SalesItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final SalesInvoiceRepository salesInvoiceRepository;
    private final SalesItemRepository salesItemRepository;
    private final SalesReturnRepository salesReturnRepository;
    private final SalesReturnItemRepository salesReturnItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final DocumentNumberService documentNumberService;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public ReturnResponse createReturn(ReturnRequest request) {
        SalesInvoice invoice = salesInvoiceRepository.findByInvoiceNo(request.getInvoiceNo())
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        List<SalesItem> items = salesItemRepository.findBySalesInvoiceId(invoice.getId());
        Map<UUID, SalesItem> itemMap = new HashMap<>();
        for (SalesItem item : items) {
            itemMap.put(item.getProduct().getId(), item);
        }

        String returnNo = documentNumberService.nextReturn(LocalDate.now());
        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setReturnNo(returnNo);
        salesReturn.setSalesInvoice(invoice);
        salesReturn.setRefundMode(request.getRefundMode());
        salesReturn.setRefundRef(request.getRefundRef());

        double total = 0;
        for (ReturnItemRequest returnItem : request.getItems()) {
            SalesItem sold = itemMap.get(returnItem.getProductId());
            if (sold == null) {
                throw new IllegalArgumentException("Product not in invoice");
            }
            if (returnItem.getQty() > sold.getQty()) {
                throw new IllegalArgumentException("Return qty exceeds sold qty");
            }
            double unitAmount = sold.getLineTotal() / sold.getQty();
            double amount = unitAmount * returnItem.getQty();
            total += amount;

            SalesReturnItem item = new SalesReturnItem();
            item.setSalesReturn(salesReturn);
            item.setProduct(sold.getProduct());
            item.setQty(returnItem.getQty());
            item.setAmount(amount);
            salesReturnItemRepository.save(item);

            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(sold.getProduct());
            movement.setTxnType(InventoryTxnType.RETURN_IN);
            movement.setQtyIn(returnItem.getQty());
            movement.setRefNo(returnNo);
            inventoryMovementRepository.save(movement);
        }

        salesReturn.setTotalAmount(total);
        salesReturnRepository.save(salesReturn);
        logAudit("RETURN_CREATE", "Return created " + returnNo);
        return new ReturnResponse(returnNo, total);
    }

    private void logAudit(String type, String message) {
        AuditLog log = new AuditLog();
        log.setEventType(type);
        log.setEventMessage(message);
        auditLogRepository.save(log);
    }
}
