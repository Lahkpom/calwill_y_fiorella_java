package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ProductAlreadyExistException;
import com.calwillyfiorella.exception.ProductVariantDoesNotExistException;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.repository.ProductVariantRepository;
import com.calwillyfiorella.util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ProductVariantService {
    private final ProductVariantRepository productVariantRepository;

    public static String targetGenderDecode(TargetGender tg) {
        return switch(tg) {
            case NINIAS     -> "Niñas";
            case NINIOS     -> "Niños";
            case HOMBRES    -> "Hombres";
            case MUJERES    -> "Mujeres";
            case UNISEX     -> "Unisex";
        };
    }

    public ProductVariantService(ProductVariantRepository productVariantRepository) { this.productVariantRepository = productVariantRepository; }

    public void addVariant(ProductVariant newVariantData, Product currentProduct) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(newVariantData, "Variant cannot be null.");

        if (currentProduct == null && newVariantData.getProduct() == null)
            throw new IllegalArgumentException("No se indicó a qué product corresponde la variante.");

        if (newVariantData.getId() == null) {
            newVariantData.setId(UUID.randomUUID());
            newVariantData.setRowStatus(RowStatus.ACTIVE);
            newVariantData.setCreatedAt(LocalDateTime.now());
            newVariantData.setUpdatedAt(null);
        } else {
            validateVariantId(newVariantData.getId());
            BaseEntity.validateRowStatus(newVariantData.getRowStatus());
            BaseEntity.validateCreatedAt(newVariantData.getCreatedAt());
        }

        if (newVariantData.getProduct() == null) {
            newVariantData.setProduct(currentProduct);
        } else {
            validateVariantProduct(currentProduct.getId(), newVariantData);
        }

        validateColor(newVariantData.getColor());
        validateSize(newVariantData.getSize());
        validateTargetGender(newVariantData.getTargetGender());
        validateSKU(newVariantData.getSku()); // Este hay que ver que sea único
        validatePrice(newVariantData.getPrice());
        validateStock(newVariantData.getStock());

        this.productVariantRepository.saveVariant(newVariantData);
    }

    public ProductVariant updateVariant(UUID variantId, ProductVariant newVariantData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(variantId        , "VariantId cannot be null.");
        Objects.requireNonNull(newVariantData   , "newVariantData cannot be null.");

        ProductVariant currentVariant = this.productVariantRepository
                .findVariant(variantId)
                .orElseThrow(ProductVariantDoesNotExistException::new);

        validateColor(newVariantData.getColor());
        validateSize(newVariantData.getSize());
        validateTargetGender(newVariantData.getTargetGender());
        validateSKU(newVariantData.getSku(), currentVariant.getId());
        validatePrice(newVariantData.getPrice());
        validateStock(newVariantData.getStock());
        BaseEntity.validateRowStatus(newVariantData.getRowStatus());

        currentVariant.setColor(newVariantData.getColor());
        currentVariant.setSize(newVariantData.getSize());
        currentVariant.setTargetGender(newVariantData.getTargetGender());
        currentVariant.setDesc(newVariantData.getDesc());
        currentVariant.setSku(newVariantData.getSku());
        currentVariant.setPrice(newVariantData.getPrice());
        currentVariant.setStock(newVariantData.getStock());
        currentVariant.setRowStatus(newVariantData.getRowStatus());

        return currentVariant;
    }

    public List<ProductVariant> getAllVariants() { return this.productVariantRepository.findAllVariants(); }

    public List<ProductVariant> getAllVariantsOf(UUID productIs) { return this.productVariantRepository.findAllVariantsOf(productIs); }

    private void validateVariantId(UUID variantId) {
        if (this.productVariantRepository.findVariant(variantId).isPresent())
            throw new ProductAlreadyExistException();
    }
    private void validateVariantProduct(UUID currentProductId, ProductVariant newVariantData) {
        if (!newVariantData.getProduct().getId().equals(currentProductId))
            throw new IllegalArgumentException("El objeto ingresado no corresponde a una variante de este producto.");
    }
    private void validateColor(Color color) {
        Objects.requireNonNull(color, "color cannot be null");
    }
    private void validateSize(NumericSize size) {
        Objects.requireNonNull(size, "size cannot be null");
    }
    private void validateTargetGender(TargetGender targetGender) {
        Objects.requireNonNull(targetGender, "targetGender cannot be null");
    }
    private void validateSKU(String sku) {
        validateSKU(sku, null);
    }
    private void validateSKU(String sku, UUID currentVariantId) {
        ValidationUtils.requireNonBlank(sku, "El SKU no puede ser nulo ni estar vacío.");

        Optional<ProductVariant> existingProduct = this.productVariantRepository.findVariant(sku);

        if (existingProduct.isPresent() && (currentVariantId == null || !existingProduct.get().getId().equals(currentVariantId)))
            throw new ProductAlreadyExistException("Ya existe una variante en la lista de productos con el SKU: " + sku);
    }
    private void validatePrice (BigDecimal amountPrice) {
        ValidationUtils.requireAmountGreaterThanZero(amountPrice, "El Precio no puede ser menor ni igual a cero.");
    }
    private void validateStock(Integer stock) {
        ValidationUtils.requireNonNegative(stock, "La cantidad ingresada para el stock no puede ser nula ni menor o igual a cero.");
    }
}
