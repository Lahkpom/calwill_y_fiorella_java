package com.calwillyfiorella.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.exception.SaleDoesNotExistException;
import com.calwillyfiorella.model.*;
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
                MenuOption.of("Gestionar Ventas"            , this::renderAdminSales),
                MenuOption.of("Gestionar Usuarios"          , () -> { System.err.println("FUNCIÓN EN DESARROLLO"); this.render(); }),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderAdminSales() {
        List<Sale> sales = saleService.getAllSales();

        List<Integer> allowedOptions = ListPrinter.renderList("TODAS LAS VENTAS", sales, true);

        List<MenuOption> options = new ArrayList<>();

        // Si no hay ventas para mostrar solo le damos las opciones del menú por defecto
        if (!allowedOptions.isEmpty())
            options.add(MenuOption.of("Ver detalles de una venta", () -> {
                Integer selected = AuxiliarFunction.requireUserOption(allowedOptions, "Número de la venta: ", true);
                renderAdminSaleDetails(sales.get(selected - 1));
            }));

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void renderAdminSaleDetails(Sale sale) {
        MenuHelper.printMenuTitle("DETALLE DE VENTA");

        System.out.println(sale.toStringComplete());

        List<MenuOption> options = List.of(
              MenuOption.of("Actualizar venta", () -> updateSale(sale))
        );

        menuHelper.renderMenuOptions(options, this::renderAdminSales);
    }
    private void updateSale(Sale currentSaleData) {
        try {
            Sale newSaleData = updateSaleForm(currentSaleData);

            if (newSaleData == null)
                throw new IllegalStateException("La venta devuelta por el formulario de actualización de ventas es un objeto nulo.");

            newSaleData.setRowStatus(currentSaleData.getRowStatus());

            if (newSaleData.getSaleNotes()      == null) newSaleData.setSaleNotes(currentSaleData.getSaleNotes());
            if (newSaleData.getSaleStatus()     == null) newSaleData.setSaleStatus(currentSaleData.getSaleStatus());
            if (newSaleData.getPaymentStatus()  == null) newSaleData.setPaymentStatus(currentSaleData.getPaymentStatus());

            saleService.updateSale(currentSaleData.getId(), newSaleData);

            System.out.println("La venta fue actualizada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la venta: " + e.getMessage());
        }

        renderAdminSaleDetails(currentSaleData);
    }
    private Sale updateSaleForm(Sale currentSaleData) {
        boolean isUpdate = true;
        boolean isStrict = false;

        return FormHelper.executeCreateOrUpdateForm("INFORMACIÓN DE LA VENTA", isUpdate, currentSaleData, () -> {
            String newNotes = InputUtils.readString(
                    FormHelper.buildPrompt(
                            "Notas",
                            currentSaleData.getSaleNotes(),
                            isUpdate
                    ),
                    isStrict
            );
            SaleStatus newSaleStatus = AuxiliarFunction.requireSaleStatus(
                    FormHelper.buildPrompt(
                            "Estado de Venta",
                            currentSaleData.getSaleStatus(),
                            isUpdate
                    ),
                    isStrict
            );
            PaymentStatus newPaymentStatus = AuxiliarFunction.requirePaymentStatus(
                    FormHelper.buildPrompt(
                            "Estado de Pago",
                            currentSaleData.getPaymentStatus(),
                            isUpdate
                    ),
                    isStrict
            );

            Sale sale = new Sale();
            sale.setSaleNotes(newNotes);
            sale.setSaleStatus(newSaleStatus);
            sale.setPaymentStatus(newPaymentStatus);
            return sale;
        });
    }

    private void renderAdminColors() {
        List<Color> colors = colorService.getAll();
        List<Integer> allowedOptions = AuxiliarFunction.toListColors(colors, true);

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay colores
        if (!allowedOptions.isEmpty())
            options.add(MenuOption.of("Ver detalle de un Color", () -> {
                Integer selected = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color: ", true);
                renderAdminColorDetails(colors.get(selected - 1));
            }));

        options.add(MenuOption.of("Crear un nuevo Color", this::createColor));

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void renderAdminColorDetails(Color color) {
        MenuHelper.printMenuTitle("DETALLE DEL COLOR");

        System.out.println(color.toStringComplete());

        List<MenuOption> options = List.of(
                MenuOption.of("Actualizar Color", () -> updateColor(color))
        );

        menuHelper.renderMenuOptions(options, this::renderAdminColors);
    }
    private void updateColor(Color currentColorData) {
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

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay productos
        if (!allowedOptions.isEmpty())
            options.add(MenuOption.of("Ver detalles de un Producto", () -> {
                Integer productIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto: ", true);
                renderAdminProductDetails(products.get(productIdx - 1));
            }));

        options.add(MenuOption.of("Crear un nuevo Producto", this::createProduct));

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void renderAdminProductDetails(Product product) {
        MenuHelper.printMenuTitle("DETALLE DEL PRODUCTO");

        List<ProductVariant> variants = this.productVariantService.getAllVariantsOf(product.getId());

        System.out.println(product.toStringComplete(variants.size()));

        List<MenuOption> options = List.of(
                MenuOption.of("Actualizar Producto"         , () -> updateProduct(product)),
                MenuOption.of("Ver Variantes del Producto"  , () -> renderAdminVariants(product))
        );

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
    private void updateProduct(Product currentProductData) {
        try {
            Product newProductData = createOrUpdateProductForm(true, currentProductData);

            if (newProductData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de actualización de productos es un objeto nulo.");

            newProductData.setCategory(Category.CALZADO);
            if (newProductData.getName()        == null) newProductData.setName(currentProductData.getName());
            if (newProductData.getShortDesc()   == null) newProductData.setShortDesc(currentProductData.getShortDesc());
            if (newProductData.getLongDesc()    == null) newProductData.setLongDesc(currentProductData.getLongDesc());
            if (newProductData.getRowStatus()   == null) newProductData.setRowStatus(currentProductData.getRowStatus());

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

    private void renderAdminVariants(Product product) {
        List<ProductVariant> variants = this.productVariantService.getAllVariantsOf(product.getId());

        List<Integer> allowedVariantOptions = ListPrinter.renderList("VARIANTES DE " + product.getName(), variants, true);

        List<MenuOption> options = new ArrayList<>();

        // Estas opciones solo si muestran si hay variantes dentro del producto
        if (!allowedVariantOptions.isEmpty()) {
            options.add(MenuOption.of("Ver Detalles de una Variante", () -> {
                Integer variantIdx = AuxiliarFunction.requireUserOption(allowedVariantOptions, "Número de la variante: ", true);
                renderAdminProductVariantDetails(product, variants.get(variantIdx - 1));
            }));
            options.add(MenuOption.of("Gestionar las Imágenes de una Variante", () -> {
                System.out.println("MÓDULO EN DESARROLLO"); renderAdminVariants(product);
            }));
        }

        options.add(MenuOption.of("Crear una nueva Variante", () -> createVariant(product)));

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
    private void renderAdminProductVariantDetails(Product product, ProductVariant productVariant) {
        MenuHelper.printMenuTitle("DETALLE DE LA VARIANTE");

        System.out.println(productVariant.toStringComplete());

        List<MenuOption> options = List.of(
                MenuOption.of("Actualizar Variante"         , () -> updateVariant(product, productVariant)),
                MenuOption.of("Gestionar las Imágenes de una Variante", () -> { System.out.println("MÓDULO EN DESARROLLO"); renderAdminVariants(product); })
        );

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
    private void updateVariant(Product product, ProductVariant currentVariantData) {
        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(true, currentVariantData);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de actualización de variantes es un objeto nulo.");

            if (newVariantData.getColor()           == null) newVariantData.setColor(currentVariantData.getColor());
            if (newVariantData.getSize()            == null) newVariantData.setSize(currentVariantData.getSize());
            if (newVariantData.getTargetGender()    == null) newVariantData.setTargetGender(currentVariantData.getTargetGender());
            if (newVariantData.getDesc()            == null) newVariantData.setDesc(currentVariantData.getDesc());
            if (newVariantData.getSku()             == null) newVariantData.setSku(currentVariantData.getSku());
            if (newVariantData.getPrice()           == null) newVariantData.setPrice(currentVariantData.getPrice());
            if (newVariantData.getStock()           == null) newVariantData.setStock(currentVariantData.getStock());
            if (newVariantData.getRowStatus()       == null) newVariantData.setRowStatus(currentVariantData.getRowStatus());

            productVariantService.updateVariant(currentVariantData.getId(), newVariantData);

            System.out.println("La variante fue actualizada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la variante: " + e.getMessage());
        }

        renderAdminVariants(product);
    }
    private void createVariant(Product product) {
        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(false, null);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de creación de variantes es un objeto nulo.");

            productVariantService.addVariant(newVariantData, product);

            System.out.println("La variante fue creada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear la variante: " + e.getMessage());
        }

        renderAdminVariants(product);
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