package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.SaleAlreadyExistException;
import com.calwillyfiorella.exception.SaleDoesNotExistException;
import com.calwillyfiorella.model.*;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SaleService {
    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) { this.saleRepository = saleRepository; }

    // SALES
    public void createSale(Sale newSaleData, Users user, List<SaleItem> saleItems) {
        Objects.requireNonNull(newSaleData);

        if (newSaleData.getId() == null) {
            newSaleData.setId(UUID.randomUUID());
            newSaleData.setRowStatus(RowStatus.ACTIVE);
            newSaleData.setCreatedAt(LocalDateTime.now());
            newSaleData.setUpdatedAt(null);
        } else {
            validateSaleId(newSaleData.getId());
            BaseEntity.validateRowStatus(newSaleData.getRowStatus());
            BaseEntity.validateCreatedAt(newSaleData.getCreatedAt());
        }

        boolean registeredUser = newSaleData.getUser() != null;

//        validateCustomerEmail



        this.saleRepository.save(newSaleData);
    }

    public void updateSale(UUID saleId, Sale newSaleData) {}

    public List<Sale> getAllSales() { return this.saleRepository.findAll(); }

    public Sale getSaleByEmail(String customerEmail) {
        return ifSaleExits(this.saleRepository.findByCustomerEmail(customerEmail));
    }
    public Sale getSaleBySaleId(UUID saleId) {
        return ifSaleExits(this.saleRepository.findBySaleId(saleId));
    }
    public Sale getSaleByUserId(UUID userId) {
        return ifSaleExits(this.saleRepository.findByUserId(userId));
    }
    private Sale ifSaleExits(Optional<Sale> saleOptional) {
        return saleOptional.orElseThrow(SaleDoesNotExistException::new);
    }

    private void validateSaleId(UUID saleId) {
        Objects.requireNonNull(saleId);

        if (this.saleRepository.findBySaleId(saleId).isPresent())
            throw new SaleAlreadyExistException();
    }



    // SALES_ITEMS
}
