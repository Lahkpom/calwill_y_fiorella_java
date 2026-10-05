package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Sale;

import java.util.*;

public class SaleRepository {
    private final List<Sale> sales;

    public SaleRepository() { sales = new ArrayList<>(); }

    public void save(Sale sale) { sales.add(sale); }

    public List<Sale> findAll() { return Collections.unmodifiableList(sales); }

    public Optional<Sale> findBySaleId(UUID saleId) {
        return this.sales.stream()
                .filter(s -> s.getId().equals(saleId))
                .findFirst();
    }

    public Optional<Sale> findByUserId(UUID userId) {
        return this.sales.stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
                .findFirst();
    }

    public Optional<Sale> findByCustomerEmail(String customerEmail) {
        return this.sales.stream()
                .filter(s -> s.getCustomerEmail().equals(customerEmail))
                .findFirst();
    }
}
