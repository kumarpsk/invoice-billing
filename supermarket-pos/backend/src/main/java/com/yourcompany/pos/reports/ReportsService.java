package com.yourcompany.pos.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> dailySales(LocalDate from, LocalDate to) {
        return jdbcTemplate.queryForList(
            "SELECT sales_date, total_sales FROM v_daily_sales WHERE sales_date BETWEEN ? AND ? ORDER BY sales_date",
            Date.valueOf(from), Date.valueOf(to));
    }

    public List<Map<String, Object>> itemSales(LocalDate from, LocalDate to) {
        return jdbcTemplate.queryForList(
            "SELECT p.name, SUM(si.qty) AS total_qty, SUM(si.line_total) AS total_amount "
                + "FROM sales_item si "
                + "JOIN sales_invoice s ON s.id = si.sales_id "
                + "JOIN products p ON p.id = si.product_id "
                + "WHERE s.bill_datetime BETWEEN ? AND ? AND s.status = 'COMPLETED' "
                + "GROUP BY p.name ORDER BY total_amount DESC",
            Date.valueOf(from), Date.valueOf(to));
    }

    public List<Map<String, Object>> gstSummary(LocalDate from, LocalDate to) {
        return jdbcTemplate.queryForList(
            "SELECT SUM(cgst_total) AS cgst_total, SUM(sgst_total) AS sgst_total, SUM(taxable_total) AS taxable_total "
                + "FROM sales_invoice WHERE bill_datetime BETWEEN ? AND ? AND status = 'COMPLETED'",
            Date.valueOf(from), Date.valueOf(to));
    }

    public List<Map<String, Object>> stock() {
        return jdbcTemplate.queryForList(
            "SELECT p.id, p.name, p.barcode, COALESCE(v.stock_qty, 0) AS stock_qty "
                + "FROM products p LEFT JOIN v_product_stock v ON v.product_id = p.id");
    }

    public List<Map<String, Object>> lowStock() {
        return jdbcTemplate.queryForList(
            "SELECT p.id, p.name, p.barcode, COALESCE(v.stock_qty, 0) AS stock_qty "
                + "FROM products p LEFT JOIN v_product_stock v ON v.product_id = p.id "
                + "WHERE COALESCE(v.stock_qty, 0) <= 5 ORDER BY stock_qty ASC");
    }
}
