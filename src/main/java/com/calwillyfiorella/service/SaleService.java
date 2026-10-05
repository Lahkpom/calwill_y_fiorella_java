package com.calwillyfiorella.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.calwillyfiorella.exception.InsufficientStockException;
import com.calwillyfiorella.exception.SaleAlreadyExistException;
import com.calwillyfiorella.exception.SaleDoesNotExistException;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Sale;
import com.calwillyfiorella.model.SaleItem;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.PaymentMethod;
import com.calwillyfiorella.model.enums.PaymentStatus;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.SaleStatus;
import com.calwillyfiorella.repository.SaleRepository;
import com.calwillyfiorella.util.ValidationUtils;

public class SaleService {
    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) { this.saleRepository = saleRepository; }

    // SALES
    public void createSale(Sale newSaleData, Users user, List<SaleItem> saleItems) {
        Objects.requireNonNull(newSaleData);
        Objects.requireNonNull(saleItems, "Sale items cannot be null");

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

        newSaleData.setUser(user);
        newSaleData.setCustomerEmail(validateSaleCustomerEmail(newSaleData.getCustomerEmail()));
        validateShippingAddress(newSaleData.getShippingAddress());
        validateSaleStatus(newSaleData.getSaleStatus());
        validatePaymentMethod(newSaleData.getPaymentMethod());
        validatePaymentStatus(newSaleData.getPaymentStatus());

        SaleTotals totals = calculateSaleTotals(saleItems, newSaleData.getShippingCost());
    adjustStockForSale(saleItems);
        List.copyOf(newSaleData.findAllItems()).forEach(newSaleData::delete);
        saleItems.forEach(item -> {
            item.setSale(newSaleData);
            newSaleData.saveItem(item);
        });
        applySaleTotals(newSaleData, totals);

        this.saleRepository.save(newSaleData);
    }

    private void adjustStockForSale(List<SaleItem> saleItems) {
        Map<UUID, ProductVariant> variantsById = new LinkedHashMap<>();
        Map<UUID, Integer> quantitiesByVariantId = new LinkedHashMap<>();

        for (SaleItem item : saleItems) {
            ProductVariant variant = Objects.requireNonNull(
                    item.getProductVariant(), "Sale item variant cannot be null");
            UUID variantId = Objects.requireNonNull(variant.getId(), "Variant id cannot be null");

            if (variant.getRowStatus() != RowStatus.ACTIVE) {
                throw new InsufficientStockException("No se puede vender una variante que no está activa.");
            }

            variantsById.putIfAbsent(variantId, variant);
            quantitiesByVariantId.merge(variantId, item.getSaleItemQuantity(), Math::addExact);
        }

        for (Map.Entry<UUID, Integer> entry : quantitiesByVariantId.entrySet()) {
            ProductVariant variant = variantsById.get(entry.getKey());
            Integer stock = variant.getStock();
            if (stock == null || stock < entry.getValue()) {
                throw new InsufficientStockException(
                        "El stock disponible no alcanza para completar la compra de la variante "
                                + variant.getSku() + ".");
            }
        }

        for (Map.Entry<UUID, Integer> entry : quantitiesByVariantId.entrySet()) {
            ProductVariant variant = variantsById.get(entry.getKey());
            variant.setStock(variant.getStock() - entry.getValue());
        }
    }

    public void updateSale(UUID saleId, Sale newSaleData) {
        Objects.requireNonNull(saleId, "Sale id cannot be null");
        Objects.requireNonNull(newSaleData, "Sale data cannot be null");
        AuthService.checkActualUserIsAdmin();

        Sale currentSale = getSaleBySaleId(saleId);
        if (newSaleData.getSaleNotes() != null) currentSale.setSaleNotes(newSaleData.getSaleNotes());
        if (newSaleData.getSaleStatus() != null) {
            validateSaleStatus(newSaleData.getSaleStatus());
            currentSale.setSaleStatus(newSaleData.getSaleStatus());
        }
        if (newSaleData.getPaymentStatus() != null) {
            validatePaymentStatus(newSaleData.getPaymentStatus());
            currentSale.setPaymentStatus(newSaleData.getPaymentStatus());
        }
    }

    public void cancelSaleByCustomer(UUID saleId, Users customer) {
        Objects.requireNonNull(saleId, "Sale id cannot be null");
        Objects.requireNonNull(customer, "Customer cannot be null");

        Users actualUser = AuthService.getActualUser();
        if (actualUser == null || !actualUser.getId().equals(customer.getId())) {
            throw new SecurityException("La sesión actual no corresponde al cliente.");
        }

        Sale sale = getSaleBySaleId(saleId);
        if (sale.getUser() == null || !sale.getUser().getId().equals(customer.getId())) {
            throw new SecurityException("No puedes cancelar una compra de otro usuario.");
        }
        if (sale.getRowStatus() != RowStatus.ACTIVE) {
            throw new IllegalStateException("Solo se pueden cancelar compras activas.");
        }
        if (sale.getSaleStatus() == SaleStatus.CANCELADO) {
            throw new IllegalStateException("La compra ya está cancelada.");
        }

        sale.setSaleStatus(SaleStatus.CANCELADO);
    }

    public List<Sale> getAllSales() { return this.saleRepository.findAll(); }

    public Sale getSaleByEmail(String customerEmail) {
        return ifSaleExits(this.saleRepository.findByCustomerEmail(customerEmail));
    }
    public Sale getSaleBySaleId(UUID saleId) {
        return ifSaleExits(this.saleRepository.findBySaleId(saleId));
    }
    public List<Sale> getSalesByUserId(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        return this.saleRepository.findByUserId(userId);
    }
    private Sale ifSaleExits(Optional<Sale> saleOptional) {
        return saleOptional.orElseThrow(SaleDoesNotExistException::new);
    }

    private void validateSaleId(UUID saleId) {
        Objects.requireNonNull(saleId);

        if (this.saleRepository.findBySaleId(saleId).isPresent())
            throw new SaleAlreadyExistException();
    }

    private String validateSaleCustomerEmail(String customerEmail) {
        ValidationUtils.requireNonBlank(customerEmail, "Customer email cannot be blank.");
        return ValidationUtils.requireValidEmail(customerEmail);
    }

    private String validateShippingAddress(String shippingAddress) {
        return ValidationUtils.requireNonBlank(shippingAddress, "Shipping address cannot be blank.");
    }

    private void validateSaleStatus(SaleStatus saleStatus) {
        Objects.requireNonNull(saleStatus, "Sale status cannot be null");
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
    }

    private void validatePaymentStatus(PaymentStatus paymentStatus) {
        Objects.requireNonNull(paymentStatus, "Payment status cannot be null");
    }

    private SaleTotals calculateSaleTotals(List<SaleItem> saleItems, BigDecimal shippingCost) {
        Objects.requireNonNull(saleItems, "Sale items cannot be null");
        if (shippingCost != null && shippingCost.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Shipping cost cannot be negative");

        int quantity = 0;
        BigDecimal subtotal = BigDecimal.ZERO;

        for (SaleItem item : saleItems) {
            Objects.requireNonNull(item, "Sale item cannot be null");
            BigDecimal unitPrice = ValidationUtils.requireAmountGreaterThanZero(
                    item.getSaleItemUnitPrice(), "Sale item unit price must be greater than zero");
            int itemQuantity = ValidationUtils.requireNonNegative(
                    item.getSaleItemQuantity(), "Sale item quantity must be greater than zero", true);
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemQuantity))
                    .setScale(2, RoundingMode.HALF_UP);

            quantity = Math.addExact(quantity, itemQuantity);
            subtotal = subtotal.add(itemSubtotal);
        }

        ValidationUtils.requireNonNegative(quantity, "Sale must contain at least one product", true);
        double discountPercent = quantity >= 3 ? 10.0 : 0.0;
        BigDecimal discount = subtotal.multiply(BigDecimal.valueOf(discountPercent / 100))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount)
                .add(shippingCost == null ? BigDecimal.ZERO : shippingCost)
                .setScale(2, RoundingMode.HALF_UP);

        for (SaleItem item : saleItems) {
            item.setSaleItemSubtotal(item.getSaleItemUnitPrice()
                .multiply(BigDecimal.valueOf(item.getSaleItemQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
            if (item.getId() == null) item.setId(UUID.randomUUID());
            if (item.getCreatedAt() == null) item.setCreatedAt(LocalDateTime.now());
        }

        return new SaleTotals(quantity, discountPercent, subtotal.setScale(2, RoundingMode.HALF_UP), total);
    }

    private void applySaleTotals(Sale sale, SaleTotals totals) {
        sale.setSaleQuantityItems(totals.quantity());
        sale.setSaleDiscounts(totals.discountPercent());
        sale.setSaleSubtotal(totals.subtotal());
        sale.setSaleTotal(totals.total());
    }

    private record SaleTotals(int quantity, double discountPercent, BigDecimal subtotal, BigDecimal total) {}



    // SALES_ITEMS
}
