package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ProductAlreadyExistException;
import com.calwillyfiorella.exception.ProductVariantDoesNotExistException;
import com.calwillyfiorella.model.*;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.repository.ProductVariantRepository;
import com.calwillyfiorella.util.ValidationUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

public class ProductVariantService {
    private static final Set<String> VALID_SCHEMES = Set.of("http", "https");

    private final ProductVariantRepository  productVariantRepository;
    private final ProductService            productService;

    public static String targetGenderDecode(TargetGender tg) {
        return switch(tg) {
            case NINIAS     -> "Niñas";
            case NINIOS     -> "Niños";
            case HOMBRES    -> "Hombres";
            case MUJERES    -> "Mujeres";
            case UNISEX     -> "Unisex";
        };
    }

    public ProductVariantService(ProductVariantRepository productVariantRepository, ProductService productService) {
        this.productVariantRepository   = productVariantRepository;
        this.productService             = productService;
    }

    public void addVariant(UUID productId, ProductVariant newVariantData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(newVariantData, "Variant cannot be null.");

        if (productId == null && newVariantData.getProduct() == null)
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

        Product currentProduct = this.productService.getProduct(productId);

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

        ProductVariant currentVariant = this.getVariant(variantId);

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

    public ProductVariant getVariant(UUID variantId) { return this.productVariantRepository.findVariant(variantId).orElseThrow(ProductVariantDoesNotExistException::new); }

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

    // VARIANT IMAGES
    public void addImage(UUID variantId, VariantImage newImageData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(newImageData, "La VariantImage ingresada no puede ser nulla.");

        if (variantId == null && newImageData.getVariant() == null)
            throw new IllegalArgumentException("No se indicó a qué variante corresponde la imágen.");

        if (newImageData.getId() == null) {
            newImageData.setId(UUID.randomUUID());
            newImageData.setRowStatus(RowStatus.ACTIVE);
            newImageData.setCreatedAt(LocalDateTime.now());
            newImageData.setUpdatedAt(null);
        } else {
            BaseEntity.validateRowStatus(newImageData.getRowStatus());
            BaseEntity.validateCreatedAt(newImageData.getCreatedAt());
        }

        ProductVariant currentVariant = this.productVariantRepository.findVariant(variantId).orElseThrow(ProductVariantDoesNotExistException::new);

        if (newImageData.getVariant() == null) {
            newImageData.setVariant(currentVariant);
        } else {
            validateImageVariant(currentVariant.getId(), newImageData);
        }

        if (!this.validateUrl(newImageData.getUrl()))
            throw new IllegalArgumentException("La URL no posee un formato válido");

        if (currentVariant.findAllImages().stream().anyMatch(img ->
                img.getRowStatus() != RowStatus.DELETED
                        &&
                        (
                                newImageData.getId().equals(img.getId())
                                || newImageData.getUrl().equalsIgnoreCase(img.getUrl())
                        )

        )) throw new IllegalArgumentException("La VariantImage ingresada ya se encuentra en la Lista de esta variante");

        newImageData.setOrder(this.validateImageOrder(currentVariant, newImageData.getOrder()));

        currentVariant.saveImage(newImageData);
    }

//    public void updateImage(UUID variantId, UUID imageId, VariantImage newImageData) {
//        ProductVariant  variant = this.getVariant(variantId);
//        VariantImage    image   = this.getImageById(variant.getId(), imageId);
//
//        if (image.getRowStatus() == newStatus) return;
//
//        image.setRowStatus(newStatus);
//    }

    private VariantImage getImageById(UUID variantId, UUID imageId) {
        if (variantId == null || imageId == null) return null;
        return this.getVariant(variantId).findImageById(imageId).orElseThrow(() -> new IllegalArgumentException("No hay una imagen en la lista con el ID proporcionado."));
    }

    public List<VariantImage> getAllImages(UUID variantId) { return this.getVariant(variantId).findAllImages(); }

    public void removeImage(UUID variantId, UUID imageId) { this.getVariant(variantId).deleteImageById(imageId); }

    private void validateImageVariant(UUID currentVariantId, VariantImage newImageData) {
        if (!newImageData.getVariant().getId().equals(currentVariantId))
            throw new IllegalArgumentException("La imágen ingresada no corresponde a esta variante.");
    }
    private boolean validateUrl(String url) {
        ValidationUtils.requireNonBlank(url, "La url no puede ser nula.");

        try {
            URI parsed = URI.create(url);
            String scheme = parsed.getScheme();
            return scheme != null && VALID_SCHEMES.contains(scheme.toLowerCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    private Integer validateImageOrder(ProductVariant variant, Integer imageOrder) {
        if (imageOrder != null) return imageOrder;

        return this.getAllImages(variant.getId()).size();
    }
}
