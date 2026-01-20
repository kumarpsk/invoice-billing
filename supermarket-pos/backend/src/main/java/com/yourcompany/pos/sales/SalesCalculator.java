package com.yourcompany.pos.sales;

import com.yourcompany.pos.master.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SalesCalculator {

    public CalculationResult calculate(List<LineInput> inputs, double billDiscount) {
        List<LineResult> lineResults = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;
        BigDecimal taxableTotal = BigDecimal.ZERO;
        BigDecimal cgstTotal = BigDecimal.ZERO;
        BigDecimal sgstTotal = BigDecimal.ZERO;

        for (LineInput input : inputs) {
            BigDecimal qty = BigDecimal.valueOf(input.getQty());
            BigDecimal price = BigDecimal.valueOf(input.getProduct().getSellingPrice());
            BigDecimal lineBase = price.multiply(qty);
            BigDecimal itemDiscount = BigDecimal.valueOf(input.getDiscountAmount());
            BigDecimal net = lineBase.subtract(itemDiscount);
            subtotal = subtotal.add(lineBase);
            discountTotal = discountTotal.add(itemDiscount);

            BigDecimal gstRate = BigDecimal.valueOf(input.getProduct().getTaxSlab().getRate());
            BigDecimal taxable;
            BigDecimal gstAmount;
            if (input.getProduct().isTaxInclusive()) {
                BigDecimal divisor = BigDecimal.ONE.add(gstRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
                taxable = net.divide(divisor, 6, RoundingMode.HALF_UP);
                gstAmount = net.subtract(taxable);
            } else {
                taxable = net;
                gstAmount = taxable.multiply(gstRate).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            }
            BigDecimal cgst = gstAmount.divide(BigDecimal.valueOf(2), 6, RoundingMode.HALF_UP);
            BigDecimal sgst = gstAmount.subtract(cgst);
            BigDecimal lineTotal = taxable.add(gstAmount);

            taxableTotal = taxableTotal.add(taxable);
            cgstTotal = cgstTotal.add(cgst);
            sgstTotal = sgstTotal.add(sgst);

            lineResults.add(new LineResult(input.getProduct(), input.getQty(), price.doubleValue(),
                itemDiscount.doubleValue(), taxable.doubleValue(), cgst.doubleValue(), sgst.doubleValue(),
                lineTotal.doubleValue()));
        }

        discountTotal = discountTotal.add(BigDecimal.valueOf(billDiscount));
        BigDecimal grandTotal = taxableTotal.add(cgstTotal).add(sgstTotal).subtract(BigDecimal.valueOf(billDiscount));
        BigDecimal rounded = grandTotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundOff = rounded.subtract(grandTotal);

        return new CalculationResult(lineResults,
            subtotal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            discountTotal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            taxableTotal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            cgstTotal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            sgstTotal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            roundOff.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            rounded.doubleValue());
    }

    @Data
    @AllArgsConstructor
    public static class LineInput {
        private Product product;
        private double qty;
        private double discountAmount;
    }

    @Data
    @AllArgsConstructor
    public static class LineResult {
        private Product product;
        private double qty;
        private double price;
        private double discountAmount;
        private double taxableAmount;
        private double cgstAmount;
        private double sgstAmount;
        private double lineTotal;
    }

    @Data
    @AllArgsConstructor
    public static class CalculationResult {
        private List<LineResult> lineResults;
        private double subtotal;
        private double discountTotal;
        private double taxableTotal;
        private double cgstTotal;
        private double sgstTotal;
        private double roundOff;
        private double grandTotal;
    }
}
