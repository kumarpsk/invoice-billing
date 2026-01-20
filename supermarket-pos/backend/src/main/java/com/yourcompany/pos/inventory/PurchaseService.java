package com.yourcompany.pos.inventory;

import com.yourcompany.pos.auth.DocumentNumberService;
import com.yourcompany.pos.common.InventoryTxnType;
import com.yourcompany.pos.master.Product;
import com.yourcompany.pos.master.ProductRepository;
import com.yourcompany.pos.master.Vendor;
import com.yourcompany.pos.master.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final DocumentNumberService documentNumberService;

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
            .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        String purchaseNo = documentNumberService.nextPurchase(request.getInvoiceDate());

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setPurchaseNo(purchaseNo);
        invoice.setVendor(vendor);
        invoice.setInvoiceDate(request.getInvoiceDate());
        purchaseInvoiceRepository.save(invoice);

        double total = 0;
        for (PurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            double lineTotal = itemReq.getQty() * itemReq.getCostPrice();
            total += lineTotal;

            PurchaseItem item = new PurchaseItem();
            item.setPurchaseInvoice(invoice);
            item.setProduct(product);
            item.setQty(itemReq.getQty());
            item.setCostPrice(itemReq.getCostPrice());
            item.setLineTotal(lineTotal);
            purchaseItemRepository.save(item);

            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(product);
            movement.setTxnType(InventoryTxnType.PURCHASE_IN);
            movement.setQtyIn(itemReq.getQty());
            movement.setRefNo(purchaseNo);
            inventoryMovementRepository.save(movement);
        }

        invoice.setTotalAmount(total);
        purchaseInvoiceRepository.save(invoice);
        return new PurchaseResponse(purchaseNo, total);
    }

    public List<PurchaseInvoice> findPurchases(LocalDate from, LocalDate to) {
        return purchaseInvoiceRepository.findByInvoiceDateBetween(from, to);
    }
}
