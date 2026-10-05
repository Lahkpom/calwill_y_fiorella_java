package com.calwillyfiorella.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.exception.SaleDoesNotExistException;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Sale;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.PaymentStatus;
import com.calwillyfiorella.model.enums.SaleStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.service.ProductVariantService;
import com.calwillyfiorella.service.SaleService;
import com.calwillyfiorella.ui.utils.FormHelper;
import com.calwillyfiorella.ui.utils.ListPrinter;
import com.calwillyfiorella.ui.utils.MenuHelper;
import com.calwillyfiorella.ui.utils.MenuOption;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;

public class AdminMenu {
    private final ProductService        productService;
    private final ColorService          colorService;
    private final ProductVariantService productVariantService;
    private final SaleService           saleService;
    private final AuthMenu              authMenu;
    private final MenuHelper            menuHelper;
    private final Runnable              mainMenu;

    public AdminMenu(
            ProductService          productService,
            ColorService            colorService,
            ProductVariantService   productVariantService,
            SaleService             saleService,
            AuthMenu                authMenu,
            MenuHelper              menuHelper,
            Runnable                mainMenu
    ) {
        this.productService         = Objects.requireNonNull(productService);
        this.colorService           = Objects.requireNonNull(colorService);
        this.productVariantService  = Objects.requireNonNull(productVariantService);
        this.saleService            = Objects.requireNonNull(saleService);
        this.authMenu               = Objects.requireNonNull(authMenu);
        this.menuHelper             = Objects.requireNonNull(menuHelper);
        this.mainMenu               = Objects.requireNonNull(mainMenu);
    }

    public void render() {
        while (!AuthService.actualUserIsAdmin()) {
            authMenu.render(true, mainMenu);
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Ver mi información"          , () -> authMenu.renderUserInfo(this::render)),
                MenuOption.of("Gestionar Productos"         , this::renderAdminProducts),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Gestionar Ventas"            , this::renderAdminSales),
                MenuOption.of("Gestionar Usuarios"          , () -> { System.out.println("FUNCIÓN EN DESARROLLO"); this.render(); }),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderAdminSales() {
        List<Sale> sales = saleService.getAllSales();
        if (sales.isEmpty()) {
            MenuHelper.printMenuTitle("VENTAS");
            System.out.println("No hay ventas registradas.");
            menuHelper.renderMenuOptions(List.of(), this::render);
            return;
        }

        List<Integer> saleOptions = ListPrinter.renderList("TODAS LAS VENTAS", sales, true);
        menuHelper.renderMenuOptions(
                List.of(MenuOption.of("Seleccionar una venta", () -> selectAdminSale(sales, saleOptions))),
                this::render
        );
    }

    private void selectAdminSale(List<Sale> sales, List<Integer> saleOptions) {
        int selected = AuxiliarFunction.requireUserOption(saleOptions, "Número de la venta: ", true);
        renderAdminSaleDetails(sales.get(selected - 1));
    }

    private void renderAdminSaleDetails(Sale sale) {
        MenuHelper.printMenuTitle("DETALLE DE VENTA");
        System.out.printf("Cliente: %s%nEmail: %s%nTeléfono: %s%n", sale.getCustomerName(), sale.getCustomerEmail(), sale.getCustomerPhone());
        System.out.printf("Dirección: %s%nEstado: %s%nPago: %s (%s)%n", sale.getShippingAddress(), sale.getSaleStatus(), sale.getPaymentStatus(), sale.getPaymentMethod());
        System.out.printf("Subtotal: %s%nEnvío: %s%nTotal: %s%nNotas: %s%n", sale.getSaleSubtotal(), sale.getShippingCost(), sale.getSaleTotal(), sale.getSaleNotes());
        System.out.println("Artículos:");
        sale.findAllItems().forEach(item -> System.out.printf("  - %s%n", item));

        List<MenuOption> options = List.of(
                MenuOption.of("Modificar notas", () -> editSaleNotes(sale)),
                MenuOption.of("Modificar estado de venta", () -> editSaleStatus(sale)),
                MenuOption.of("Modificar estado de pago", () -> editPaymentStatus(sale))
        );
        menuHelper.renderMenuOptions(options, this::renderAdminSales);
    }

    private void editSaleNotes(Sale sale) {
        String notes = InputUtils.readString("Nuevas notas (vacío para conservar las actuales): ", false);
        if (notes == null) {
            renderAdminSaleDetails(sale);
            return;
        }

        Sale changes = new Sale();
        changes.setSaleNotes(notes);
        updateSale(sale, changes);
    }

    private void editSaleStatus(Sale sale) {
        SaleStatus[] statuses = SaleStatus.values();
        MenuHelper.printMenuTitle("ESTADOS DE VENTA");
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("Opción %d. %s%n", i + 1, statuses[i]);
        }
        int selected = AuxiliarFunction.requireUserOption(statuses.length, "Nuevo estado de venta: ", true);

        Sale changes = new Sale();
        changes.setSaleStatus(statuses[selected - 1]);
        updateSale(sale, changes);
    }

    private void editPaymentStatus(Sale sale) {
        PaymentStatus[] statuses = PaymentStatus.values();
        MenuHelper.printMenuTitle("ESTADOS DE PAGO");
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("Opción %d. %s%n", i + 1, statuses[i]);
        }
        int selected = AuxiliarFunction.requireUserOption(statuses.length, "Nuevo estado de pago: ", true);

        Sale changes = new Sale();
        changes.setPaymentStatus(statuses[selected - 1]);
        updateSale(sale, changes);
    }

    private void updateSale(Sale sale, Sale changes) {
        try {
            saleService.updateSale(sale.getId(), changes);
            System.out.println("La venta fue actualizada.");
        } catch (NullPointerException | IsNotAnAdminException | SaleDoesNotExistException e) {
            System.err.println("Error al actualizar la venta: " + e.getMessage());
        }
        renderAdminSaleDetails(sale);
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

        List<ProductVariant> variants = product.findAllVariants();

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
            if (newVariantData.getDesc() == null) newVariantData.setDesc(currentVariantData.getDesc());
            if (newVariantData.getSku() == null) newVariantData.setSku(currentVariantData.getSku());
            if (newVariantData.getPrice() == null) newVariantData.setPrice(currentVariantData.getPrice());
            if (newVariantData.getStock() == null) newVariantData.setStock(currentVariantData.getStock());
            if (newVariantData.getRowStatus() == null) newVariantData.setRowStatus(currentVariantData.getRowStatus());

            productVariantService.updateVariant(product.getId(), currentVariantData.getId(), newVariantData);

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

            productVariantService.addVariant(product.getId(), newVariantData);

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
                            isUpdate ? currentVariantData.getDesc() : null,
                            isUpdate
                    ),
                    isStrict
            );
            String newSKU = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "SKU",
                            isUpdate ? currentVariantData.getSku() : null,
                            isUpdate
                    ),
                    isStrict
            );
            BigDecimal newPrice = InputUtils.readPrice(
                    FormHelper.buildPrompt(
                            "Precio",
                            isUpdate ? currentVariantData.getPrice() : null,
                            isUpdate
                    ),
                    isStrict
            );
            Integer newStock = InputUtils.readInt(
                    FormHelper.buildPrompt(
                            "Stock",
                            isUpdate ? currentVariantData.getStock() : null,
                            isUpdate
                    ),
                    isStrict
            );

            ProductVariant variant = new ProductVariant();
            variant.setColor(newColor);
            variant.setSize(newSize);
            variant.setTargetGender(newTargetGender);
            variant.setDesc(newDesc);
            variant.setSku(newSKU);
            variant.setPrice(newPrice);
            variant.setStock(newStock);
            return variant;
        });
    }
}