package com.calwillyfiorella.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.calwillyfiorella.model.Sale;

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

    public List<Sale> findByUserId(UUID userId) {
        return this.sales.stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
                .toList();
    }

    public Optional<Sale> findByCustomerEmail(String customerEmail) {
        return this.sales.stream()
                .filter(s -> s.getCustomerEmail().equals(customerEmail))
                .findFirst();
    }
}
