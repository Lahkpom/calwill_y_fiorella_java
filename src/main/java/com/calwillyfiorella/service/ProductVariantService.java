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
import com.calwillyfiorella.util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * No tiene ProductVariantRepository ya que cada producto es owner de su propia lista de variantes
 * Se utiliza el ProductService para traer al producto con el que se esté trabajando y ahí llamar a sus funciones internas
 */
public class ProductVariantService {
    private final ProductService productService;

    public static String targetGenderDecode(TargetGender tg) {
        return switch(tg) {
            case NINIAS     -> "Niñas";
            case NINIOS     -> "Niños";
            case HOMBRES    -> "Hombres";
            case MUJERES    -> "Mujeres";
            case UNISEX     -> "Unisex";
        };
    }

    public ProductVariantService(ProductService productService) { this.productService = productService; }

    public void addVariant(UUID productId, ProductVariant newVariantData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(productId        , "ProductId cannot be null.");
        Objects.requireNonNull(newVariantData   , "Variant cannot be null.");

        Product currentProduct = this.productService.getProduct(productId);

        if (newVariantData.getId() == null) {
            newVariantData.setId(UUID.randomUUID());
            newVariantData.setRowStatus(RowStatus.ACTIVE);
            newVariantData.setCreatedAt(LocalDateTime.now());
            newVariantData.setUpdatedAt(null);
        } else {
            validateVariantId(currentProduct, newVariantData.getId());
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
        validateSKU(currentProduct, newVariantData.getSku()); // Este hay que ver que sea único
        validatePrice(newVariantData.getPrice());
        validateStock(newVariantData.getStock());

        currentProduct.saveVariant(newVariantData);
    }

    public ProductVariant addVariant( UUID productId, Color color, NumericSize size, TargetGender targetGender, String variantDesc, String variantSku, BigDecimal variantPrice, Integer variantStock) {
        Product product = this.productService.getProduct(productId);

        if (variantSku == null || product.findAllVariants().stream().anyMatch(v -> v.getSku().equals(variantSku))) throw new IllegalArgumentException("Ya existe en la lista de variantes una variante con la misma SKU.");

        ProductVariant pv = new ProductVariant(product, color, size, targetGender, variantDesc, variantSku, variantPrice, variantStock);

        product.saveVariant(pv);

        return pv;
    }

    public ProductVariant updateVariant(UUID productId, UUID variantId, ProductVariant newVariantData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(productId        , "ProductId cannot be null.");
        Objects.requireNonNull(variantId        , "VariantId cannot be null.");
        Objects.requireNonNull(newVariantData   , "newVariantData cannot be null.");

        Product currentProduct = this.productService.getProduct(productId);

        ProductVariant currentVariant = currentProduct.findVariant(variantId).orElseThrow(ProductVariantDoesNotExistException::new);

        validateVariantId(currentProduct, newVariantData.getId());

        validateVariantProduct(currentProduct.getId(), newVariantData);
        validateColor(newVariantData.getColor());
        validateSize(newVariantData.getSize());
        validateTargetGender(newVariantData.getTargetGender());
        validateSKU(currentProduct, newVariantData.getSku(), currentVariant.getId());
        validatePrice(newVariantData.getPrice());
        validateStock(newVariantData.getStock());

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

    private void validateVariantId(Product product, UUID variantId) {
        if (product.findVariant(variantId).isPresent())
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
    private void validateSKU(Product product, String sku) {
        validateSKU(product, sku, null);
    }
    private void validateSKU(Product product, String sku, UUID currentVariantId) {
        ValidationUtils.requireNonBlank(sku, "El SKU no puede ser nulo ni estar vacío.");

        Optional<ProductVariant> existingProduct = product.findVariant(sku);

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
