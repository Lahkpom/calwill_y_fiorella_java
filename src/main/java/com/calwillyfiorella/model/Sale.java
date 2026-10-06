package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.PaymentMethod;
import com.calwillyfiorella.model.enums.PaymentStatus;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Sale extends BaseEntity {
    private final List<SaleItem> items = new ArrayList<>();

    private UUID            saleId;
    private Users           user;
    private String          customerName;
    private String          customerEmail;
    private String          customerPhone;
    private String          shippingAddress;
    private BigDecimal      shippingCost;
    private Integer         saleQuantityItems;
    private Double          saleDiscounts;
    private BigDecimal      saleSubtotal;
    private BigDecimal      saleTotal;
    private String          saleNotes;
    private LocalDateTime   saleShippedDate;
    private SaleStatus      saleStatus;
    private PaymentMethod   paymentMethod;
    private PaymentStatus   paymentStatus;
    private String          mpPaymentId;
    private String          mpPreferenceId;

    public Sale() {}

    public Sale(
            Users           user,
            String          customerName,
            String          customerEmail,
            String          customerPhone,
            String          shippingAddress,
            BigDecimal      shippingCost,
            Integer         saleQuantityItems,
            Double          saleDiscounts,
            BigDecimal      saleSubtotal,
            BigDecimal      saleTotal,
            String          saleNotes,
            LocalDateTime   saleShippedDate,
            SaleStatus      saleStatus,
            PaymentMethod   paymentMethod,
            PaymentStatus   paymentStatus,
            String          mpPaymentId,
            String          mpPreferenceId
    ) {
        this(
                UUID.randomUUID(),
                user,
                customerName,
                customerEmail,
                customerPhone,
                shippingAddress,
                shippingCost,
                saleQuantityItems,
                saleDiscounts,
                saleSubtotal,
                saleTotal,
                saleNotes,
                saleShippedDate,
                saleStatus,
                paymentMethod,
                paymentStatus,
                mpPaymentId,
                mpPreferenceId,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    public Sale(
            UUID            saleId,
            Users           user,
            String          customerName,
            String          customerEmail,
            String          customerPhone,
            String          shippingAddress,
            BigDecimal      shippingCost,
            Integer         saleQuantityItems,
            Double          saleDiscounts,
            BigDecimal      saleSubtotal,
            BigDecimal      saleTotal,
            String          saleNotes,
            LocalDateTime   saleShippedDate,
            SaleStatus      saleStatus,
            PaymentMethod   paymentMethod,
            PaymentStatus   paymentStatus,
            String          mpPaymentId,
            String          mpPreferenceId,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.saleId             = saleId;
        this.user               = user;
        this.customerName       = customerName;
        this.customerEmail      = customerEmail;
        this.customerPhone      = customerPhone;
        this.shippingAddress    = shippingAddress;
        this.shippingCost       = shippingCost;
        this.saleQuantityItems  = saleQuantityItems;
        this.saleDiscounts      = saleDiscounts;
        this.saleSubtotal       = saleSubtotal;
        this.saleTotal          = saleTotal;
        this.saleNotes          = saleNotes;
        this.saleShippedDate    = saleShippedDate;
        this.saleStatus         = saleStatus;
        this.paymentMethod      = paymentMethod;
        this.paymentStatus      = paymentStatus;
        this.mpPaymentId        = mpPaymentId;
        this.mpPreferenceId     = mpPreferenceId;
    }

    @Override
    public String toString() {
        return String.format(
                "{ Nombre: %s, Email: %s, Teléfono: %s, Productos: %d, Total: %s, Estado de Venta: %s, Estado de Pago: %s }",
                this.customerName,
                this.customerEmail,
                this.customerPhone,
                this.saleQuantityItems,
                this.saleTotal,
                this.saleStatus,
                this.paymentStatus
        );
    }

    public String toStringComplete() {
        StringBuilder sb = new StringBuilder();

        sb.append("""
            {
                ID          : (%s).
                Cliente     : %s.
                Email       : %s.
                Teléfono    : %s.
                Dirección   : %s.
                Estado      : %s.
                Pago        : %s (%s).
                Subtotal    : $%s.-
                Envío       : $%s.-
                Total       : $%s.-
                Notas       : %s.
                Artículos   : (%d)
            """.formatted(
                this.saleId,
                this.customerName,
                this.customerEmail,
                this.customerPhone,
                this.shippingAddress,
                this.saleStatus,
                this.paymentStatus, this.paymentMethod,
                this.saleSubtotal,
                this.shippingCost,
                this.saleTotal,
                this.saleNotes != null ? this.saleNotes : "Sin notas",
                this.saleQuantityItems
        ));

        findAllItems().forEach(item -> sb.append(
                String.format("        - %s%n", item)
        ));

        sb.append("}");

        return sb.toString();
    }

    public UUID             getId               () { return saleId; }
    public Users            getUser             () { return user; }
    public String           getCustomerName     () { return customerName; }
    public String           getCustomerEmail    () { return customerEmail; }
    public String           getCustomerPhone    () { return customerPhone; }
    public String           getShippingAddress  () { return shippingAddress; }
    public BigDecimal       getShippingCost     () { return shippingCost; }
    public Integer          getSaleQuantityItems() { return saleQuantityItems; }
    public Double           getSaleDiscounts    () { return saleDiscounts; }
    public BigDecimal       getSaleSubtotal     () { return saleSubtotal; }
    public BigDecimal       getSaleTotal        () { return saleTotal; }
    public String           getSaleNotes        () { return saleNotes; }
    public LocalDateTime    getSaleShippedDate  () { return saleShippedDate; }
    public SaleStatus       getSaleStatus       () { return saleStatus; }
    public PaymentMethod    getPaymentMethod    () { return paymentMethod; }
    public PaymentStatus    getPaymentStatus    () { return paymentStatus; }
    public String           getMpPaymentId      () { return mpPaymentId; }
    public String           getMpPreferenceId   () { return mpPreferenceId; }

    public void setId(UUID saleId) { this.saleId = saleId; }
    public void setUser(Users user) { this.user = user; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; super.afterUpdate(); }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; super.afterUpdate(); }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public void setSaleQuantityItems(Integer saleQuantityItems) { this.saleQuantityItems = saleQuantityItems; }
    public void setSaleDiscounts(Double saleDiscounts) { this.saleDiscounts = saleDiscounts; }
    public void setSaleSubtotal(BigDecimal saleSubtotal) { this.saleSubtotal = saleSubtotal; }
    public void setSaleTotal(BigDecimal saleTotal) { this.saleTotal = saleTotal; }
    public void setSaleNotes(String saleNotes) { this.saleNotes = saleNotes; super.afterUpdate(); }
    public void setSaleShippedDate(LocalDateTime saleShippedDate) { this.saleShippedDate = saleShippedDate; }
    public void setSaleStatus(SaleStatus saleStatus) { this.saleStatus = saleStatus; super.afterUpdate(); }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; super.afterUpdate(); }
    public void setMpPaymentId(String mpPaymentId) { this.mpPaymentId = mpPaymentId; super.afterUpdate(); }
    public void setMpPreferenceId(String mpPreferenceId) { this.mpPreferenceId = mpPreferenceId; super.afterUpdate(); }

    // Funciones para List<SaleItem>
    public void saveItem(SaleItem item) { this.items.add(item); }

    public List<SaleItem> findAllItems() { return Collections.unmodifiableList(this.items); }

    public Optional<SaleItem> findItem(UUID itemId) {
        return this.items.stream()
                .filter(v -> v.getId().equals(itemId))
                .findFirst();
    }

    public void delete(SaleItem item) { this.items.remove(item); }
}
