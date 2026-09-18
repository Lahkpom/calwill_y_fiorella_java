package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

public class ProductVariant extends BaseEntity {
    private final UUID                variantId;
    private final Product             product;
    private final List<VariantImage>  images = new ArrayList<>();

    private Color           color;
    private Size            size;
    private TargetGender    targetGender;
    private String          variantDesc;
    private String          variantSku;
    private BigDecimal      variantPrice;
    private Integer         variantStock;

    public ProductVariant(
            Product             product,
            Color               color,
            Size                size,
            TargetGender        targetGender,
            String              variantDesc,
            String              variantSku,
            BigDecimal          variantPrice,
            Integer             variantStock
    ) {
        this(
                UUID.randomUUID(),
                product,
                color,
                size,
                targetGender,
                variantDesc,
                variantSku,
                variantPrice,
                variantStock,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    public ProductVariant(
            UUID            variantId,
            Product         product,
            Color           color,
            Size            size,
            TargetGender    targetGender,
            String          variantDesc,
            String          variantSku,
            BigDecimal      variantPrice,
            Integer         variantStock,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.variantId      = Objects.requireNonNull(variantId, "El variantId no puede ser nulo");
        this.product        = Objects.requireNonNull(product, "Se debe indicar un producto válido al que pertenece la variante");
        this.color          = this.validateColor(color);
        this.size           = this.validateSize(size);
        this.targetGender   = this.validateTargetGender(targetGender);
        this.variantDesc    = this.validateVariantDesc(variantDesc);
        this.variantSku     = this.validateSKU(variantSku);
        this.variantPrice   = this.validateAmountPrice(variantPrice);
        this.variantStock   = this.validateStock(variantStock);
    }

    @Override
    public String toString() {
        return String.format(
                "Producto: %s - Categoría: %s - Para: %s - Color: %s - Talle: %s - Precio: %s - Stock: %d - Imágenes: %d - Estado: %s",
                this.product.getProductName(),
                this.product.getProductCategory(),
                targetGenderDecode(this.targetGender),
                this.color.getColorName(),
                this.size.getSize(),
                this.variantPrice,
                this.variantStock,
                this.images.size(),
                this.rowStatus
        );
    }

    public static String targetGenderDecode(TargetGender tg) {
        return switch(tg) {
            case NINIAS     -> "Niñas";
            case NINIOS     -> "Niños";
            case HOMBRES    -> "Hombres";
            case MUJERES    -> "Mujeres";
            case UNISEX     -> "Unisex";
        };
    }

//    COMPLEX FUNCTIONS
    public void addImage(VariantImage image) {
        if (image == null)
            throw new NullPointerException("La VariantImage ingresada no puede ser nulla.");

        if (!image.getVariant().getVariantId().equals(this.variantId))
            throw new IllegalArgumentException("La VariantImage ingresada no corresponde a esta variante.");

        if (this.images.stream().anyMatch(img ->
                (
                        image.getImageId().equals(img.getImageId())
                ) || (
                        image.getImageUrl().equalsIgnoreCase(img.getImageUrl())
                )
        )) throw new IllegalArgumentException("La VariantImage ingresada ya se encuentra en la Lista de esta variante");

        this.images.add(image);
    }

    public VariantImage addImage(String imageUrl) {
        if (this.images.stream().anyMatch(img -> img.getImageUrl().equalsIgnoreCase(imageUrl)))
            throw new IllegalArgumentException("La imagen ingresada ya existe en la lista de esta variante.");

        VariantImage vi = new VariantImage(this, imageUrl, this.images.size() + 1);

        this.images.add(vi);

        return vi;
    }

    public boolean removeImage(UUID imageId) {
        return this.images.removeIf(img -> imageId.equals(img.getImageId()));
    }

    public void changeImageStatus(UUID imageId, RowStatus newStatus) {
        VariantImage image = this.getImageById(imageId);

        if (image == null)
            throw new IllegalArgumentException("La URL ingresada no corresponde a una imagen de esta variante.");

        if (image.getRowStatus() == newStatus) return;

        switch (newStatus) {
            case ACTIVE     -> image.activate();
            case INACTIVE   -> image.deactive();
            case DELETED    -> image.delete();
        }
    }

    public void incrementStock (Integer stock) {
        this.variantStock += this.validateStock(stock);
        this.afterUpdate();
    }

    public void decrementStock (Integer stock) {
        this.variantStock -= ValidationUtils.requireSufficientQuantity(
                this.variantStock,
                stock,
                "La cantidad ingresada para disminuir el stock no puede ser nula ni hacer que el resultado final deje a stock menor a cero."
        );
        this.afterUpdate();
    }
//    COMPLEX FUNCTIONS

//    UTIL FUNCTIONS
    private VariantImage getImageById(UUID imageId) {
        if (imageId == null) return null;

        VariantImage dummy = null;
        for (VariantImage img : this.images) {
            if (img.getImageId().equals(imageId)) {
                dummy = img;
                break;
            }
        }
        return dummy;
    }
//    UTIL FUNCTIONS

//    SETTERS
    public void setColor(Color color) {
        this.color = this.validateColor(color);
        this.afterUpdate();
    }

    public void setSize(Size size) {
        this.size = this.validateSize(size);
        this.afterUpdate();
    }

    public void setTargetGender(TargetGender targetGender) {
        this.targetGender = this.validateTargetGender(targetGender);
        this.afterUpdate();
    }

    public void setVariantDesc(String variantDesc) {
        this.variantDesc = this.validateVariantDesc(variantDesc);
        this.afterUpdate();
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = this.validateSKU(variantSku);
        this.afterUpdate();
    }

    public void setVariantPrice(BigDecimal variantPrice) {
        this.variantPrice = this.validateAmountPrice(variantPrice);
        this.afterUpdate();
    }
//    SETTERS

//    VALIDACIONES
    private Integer validateStock(Integer stock) {
        return ValidationUtils.requireNonNegative(stock, "La cantidad ingresada para incrementar el stock no puede ser nula ni menor o igual a cero.");
    }

    private String validateSKU(String sku) {
        return ValidationUtils.requireNonBlank(sku, "El SKU no puede ser nulo ni estar vacío.");
    }

    private Color validateColor(Color color) {
        return Objects.requireNonNull(color, "color cannot be null");
    }

    private Size validateSize(Size size) {
        return Objects.requireNonNull(size, "size cannot be null");
    }

    private TargetGender validateTargetGender(TargetGender targetGender) {
        return Objects.requireNonNull(targetGender, "targetGender cannot be null");
    }

    private String validateVariantDesc(String variantDesc) {
        return ValidationUtils.requireNonBlank(variantDesc, "La descripción larga del producto no puede estra vacía.");
    }

    private BigDecimal validateAmountPrice (BigDecimal amountPrice) {
        return ValidationUtils.requireAmountGreaterThanZero(amountPrice, "El Precio no puede ser menor ni igual a cero.");
    }
//    VALIDACIONES

//    GETTERS
    public UUID                 getVariantId        () { return this.variantId; }
    public Product              getProduct          () { return this.product; }
    public List<VariantImage>   getImages           () { return Collections.unmodifiableList(this.images); }
    public List<VariantImage>   getAvailableImages  () { return this.images.stream().filter(s -> s.getRowStatus() == RowStatus.ACTIVE).toList(); }
    public Color                getColor            () { return this.color; }
    public Size                 getSize             () { return this.size; }
    public TargetGender         getTargetGender     () { return this.targetGender; }
    public String               getVariantDesc      () { return this.variantDesc; }
    public String               getVariantSku       () { return this.variantSku; }
    public BigDecimal           getVariantPrice     () { return this.variantPrice; }
    public Integer              getVariantStock     () { return this.variantStock; }

    public void toListAvailableImages () {
        List<VariantImage> availableImages = this.getAvailableImages();
        System.out.println("#### LISTADO DE IMAGENES DISPONIBLES ####");
        for (int i = 0; i < availableImages.size(); i++) {
            System.out.println((i + 1) + ". " + availableImages.get(i));
        }
    }
//    GETTERS
}