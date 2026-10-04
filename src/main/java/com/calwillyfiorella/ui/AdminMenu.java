package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;
import com.calwillyfiorella.ui.utils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminMenu {
    private final ProductService    productService;
    private final ColorService      colorService;
    private final AuthMenu          authMenu;
    private final MenuHelper        menuHelper;
    private final Runnable          mainMenu;

    public AdminMenu(
            ProductService  productService,
            ColorService    colorService,
            AuthMenu        authMenu,
            MenuHelper      menuHelper,
            Runnable        mainMenu
    ) {
        this.productService = Objects.requireNonNull(productService);
        this.colorService   = Objects.requireNonNull(colorService);
        this.authMenu       = Objects.requireNonNull(authMenu);
        this.menuHelper     = Objects.requireNonNull(menuHelper);
        this.mainMenu       = Objects.requireNonNull(mainMenu);
    }

    public void render() {
        while (!AuthService.actualUserIsAdmin()) {
            authMenu.render(true, mainMenu);
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Ver mi información"          , () -> authMenu.renderUserInfo(this::render)),
                MenuOption.of("Gestionar Productos"         , this::renderAdminProducts),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Gestionar Ventas"            , () -> { System.out.println("FUNCIÓN EN DESARROLLO"); this.render(); }),
                MenuOption.of("Gestionar Usuarios"          , () -> { System.out.println("FUNCIÓN EN DESARROLLO"); this.render(); }),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderAdminColors() {
        List<Color> colors = colorService.getAll();
        List<Integer> allowedOptions = AuxiliarFunction.toListColors(colors, true);

        boolean thereAreColors = !allowedOptions.isEmpty();

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay colores
        if (thereAreColors) {
            options.add(MenuOption.of("Editar un ProColorducto", () -> updateColor(colors, allowedOptions)));
        }

        options.add(MenuOption.of("Crear un nuevo Color", this::createColor));

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateColor(List<Color> colors, List<Integer> allowedOptions) {
        Integer colorIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color: ", false);
        Color currentColorData = colors.get(colorIdx - 1);

        try {
            Color newColorData = createOrUpdateColorForm(true, currentColorData);

            if (newColorData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de actualización de productos es un objeto nulo.");

            if (newColorData.getName() == null) newColorData.setName(currentColorData.getName());
            if (newColorData.getDesc() == null) newColorData.setDesc(currentColorData.getDesc());
            if (newColorData.getCode() == null) newColorData.setCode(currentColorData.getCode());
            if (newColorData.getRowStatus() == null) newColorData.setRowStatus(currentColorData.getRowStatus());

            colorService.updateColor(currentColorData.getId(), newColorData);

            System.out.println("El color fue actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void createColor() {
        try {
            Color newColorData = createOrUpdateColorForm(false, null);

            if (newColorData == null)
                throw new IllegalStateException("El Color devuelto por el formulario de creación de Colores es un objeto nulo.");

            colorService.createColor(newColorData);

            System.out.println("el Color fue creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear el Color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private Color createOrUpdateColorForm(boolean isUpdate, Color currentColorData) {
        boolean isStrict = !isUpdate;

        return FormHelper.executeCreateOrUpdateForm("COLOR", isUpdate, currentColorData, () -> {
            String newName = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Nombre",
                            isUpdate ? currentColorData.getName() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newDesc = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Descripción",
                            isUpdate ? currentColorData.getDesc() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newCode = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Código",
                            isUpdate ? currentColorData.getCode() : null,
                            isUpdate
                    ),
                    isStrict
            );

            Color color = new Color();
            color.setName(newName);
            color.setDesc(newDesc);
            color.setCode(newCode);
            return color;
        });
    }

    private void renderAdminProducts() {
        List<Product> products = productService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE PRODUCTOS", products, true);

        boolean thereAreProducts = !allowedOptions.isEmpty();

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay productos
        if (thereAreProducts) {
            options.add(MenuOption.of("Editar un Producto", () -> updateProduct(products, allowedOptions)));
            options.add(MenuOption.of("Gestionar Variantes de un Producto", () -> renderAdminVariants(products, allowedOptions)));
        }

        options.add(MenuOption.of("Crear un nuevo Producto", this::createProduct));

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateProduct(List<Product> products, List<Integer> allowedOptions) {
        Integer productIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto: ", false);
        Product currentProductData = products.get(productIdx - 1);

        try {
            Product newProductData = createOrUpdateProductForm(true, currentProductData);

            if (newProductData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de actualización de productos es un objeto nulo.");

            newProductData.setCategory(Category.CALZADO);
            if (newProductData.getName() == null) newProductData.setName(currentProductData.getName());
            if (newProductData.getShortDesc() == null) newProductData.setShortDesc(currentProductData.getShortDesc());
            if (newProductData.getLongDesc() == null) newProductData.setLongDesc(currentProductData.getLongDesc());
            if (newProductData.getRowStatus() == null) newProductData.setRowStatus(currentProductData.getRowStatus());

            productService.updateProduct(currentProductData.getId(), newProductData);

            System.out.println("El producto fue actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private void createProduct() {
        try {
            Product newProductData = createOrUpdateProductForm(false, null);

            if (newProductData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de creación de productos es un objeto nulo.");

            productService.createProduct(newProductData);

            System.out.println("el Producto fue creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private Product createOrUpdateProductForm(boolean isUpdate, Product currentProductData) {
        boolean isStrict = !isUpdate;

        return FormHelper.executeCreateOrUpdateForm("PRODUCTO", isUpdate, currentProductData, () -> {
            // CATEGORY (POR EL MOMENTO SOLO TENEMOS CATEGORÍA CALZADO)
            Category newCategory = Category.CALZADO;
            String newName = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Nombre",
                            isUpdate ? currentProductData.getName() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newShortDesc = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Descripción Corta",
                            isUpdate ? currentProductData.getShortDesc() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newLongDesc = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Descripción Larga",
                            isUpdate ? currentProductData.getLongDesc() : null,
                            isUpdate
                    ),
                    isStrict
            );

            Product product = new Product();
            product.setCategory(newCategory);
            product.setName(newName);
            product.setShortDesc(newShortDesc);
            product.setLongDesc(newLongDesc);
            return product;
        });
    }

    private void renderAdminVariants(List<Product> products, List<Integer> allowedProductOptions) {
        Integer idx = AuxiliarFunction.requireUserOption(allowedProductOptions, "Número del producto: ", true);
        Product product = products.get(idx - 1);

        List<ProductVariant> variants = product.getVariants();

        List<Integer> allowedVariantOptions = ListPrinter.renderList("VARIANTES DE " + product.getName(), variants, true);

        boolean thereAreVariants = !allowedVariantOptions.isEmpty();

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay variantes dentro del producto
        if (thereAreVariants) {
            options.add(MenuOption.of("Editar una Variante", () -> updateVariant(product, variants, allowedVariantOptions)));
            options.add(MenuOption.of("Gestionar las Imágenes de una Variante", null));
        }

        options.add(MenuOption.of("Crear una nueva Variante", () -> createVariant(product)));

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
    private void updateVariant(Product product, List<ProductVariant> variants, List<Integer> allowedVariantOptions) {
        Integer variantIdx = AuxiliarFunction.requireUserOption(allowedVariantOptions, "Número de la variante: ", true);
        ProductVariant currentVariantData = variants.get(variantIdx - 1);

        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(true, currentVariantData);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de actualización de variantes es un objeto nulo.");

            if (newVariantData.getColor() == null) newVariantData.setColor(currentVariantData.getColor());
            if (newVariantData.getSize() == null) newVariantData.setSize(currentVariantData.getSize());
            if (newVariantData.getTargetGender() == null) newVariantData.setTargetGender(currentVariantData.getTargetGender());
            if (newVariantData.getVariantDesc() == null) newVariantData.setVariantDesc(currentVariantData.getVariantDesc());
            if (newVariantData.getVariantSku() == null) newVariantData.setVariantSku(currentVariantData.getVariantSku());
            if (newVariantData.getVariantPrice() == null) newVariantData.setVariantPrice(currentVariantData.getVariantPrice());
            if (newVariantData.getVariantStock() == null) newVariantData.setVariantStock(currentVariantData.getVariantStock());
            if (newVariantData.getRowStatus() == null) newVariantData.setRowStatus(currentVariantData.getRowStatus());

            product.updateVariant(currentVariantData.getVariantId(), newVariantData);

            System.out.println("La variante fue actualizada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la variante: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private void createVariant(Product product) {
        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(false, null);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de creación de variantes es un objeto nulo.");

            newVariantData.setProduct(product);

            product.addVariant(newVariantData);

            System.out.println("La variante fue creada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear la variante: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private ProductVariant createOrUpdateVariantForm(boolean isUpdate, ProductVariant currentVariantData) {
        boolean isStrict = !isUpdate;

        return FormHelper.executeCreateOrUpdateForm("VARIANTE", isUpdate, currentVariantData, () -> {
            Color newColor = AuxiliarFunction.requireColor(
                    colorService.getAll(),
                    FormHelper.buildPrompt(
                            "Color",
                            isUpdate ? currentVariantData.getColor() : null,
                            isUpdate
                    ),
                    true,
                    isStrict
            );
            NumericSize newSize = AuxiliarFunction.requireNumericSize(
                    FormHelper.buildPrompt(
                            "Talle",
                            isUpdate ? currentVariantData.getSize() : null,
                            isUpdate
                    ),
                    isStrict
            );
            TargetGender newTargetGender = AuxiliarFunction.requireTargetGender(
                    FormHelper.buildPrompt(
                            "Género",
                            isUpdate ? currentVariantData.getTargetGender() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newDesc = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Descripción",
                            isUpdate ? currentVariantData.getVariantDesc() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newSKU = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "SKU",
                            isUpdate ? currentVariantData.getVariantSku() : null,
                            isUpdate
                    ),
                    isStrict
            );
            BigDecimal newPrice = InputUtils.readPrice(
                    FormHelper.buildPrompt(
                            "Precio",
                            isUpdate ? currentVariantData.getVariantPrice() : null,
                            isUpdate
                    ),
                    isStrict
            );
            Integer newStock = InputUtils.readInt(
                    FormHelper.buildPrompt(
                            "Stock",
                            isUpdate ? currentVariantData.getVariantStock() : null,
                            isUpdate
                    ),
                    isStrict
            );

            ProductVariant variant = new ProductVariant();
            variant.setColor(newColor);
            variant.setSize(newSize);
            variant.setTargetGender(newTargetGender);
            variant.setVariantDesc(newDesc);
            variant.setVariantSku(newSKU);
            variant.setVariantPrice(newPrice);
            variant.setVariantStock(newStock);
            return variant;
        });
    }
}