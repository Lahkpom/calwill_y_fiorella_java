package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.ui.viewUtils.ListPrinter;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.ui.menuUtils.*;
import com.calwillyfiorella.util.InputUtils;

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
                MenuOption.of("Gestionar Productos"         , this::renderAdminProducts),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Gestionar Ventas"            , () -> System.out.println("FUNCIÓN EN DESARROLLO")),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderAdminColors() {
        List<Color> colors = colorService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE COLORES", colors, true);

        if (allowedOptions.isEmpty()) render();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar el Nombre de un Color"        , () -> updateColorName(colors, allowedOptions)),
                MenuOption.of("Editar la Descripción de un Color"   , () -> updateColorDesc(colors, allowedOptions)),
                MenuOption.of("Editar el Código de un Color"        , () -> updateColorCode(colors, allowedOptions)),
                MenuOption.of("Cambiar el Estado de un Color"       , () -> updateColorStatus(colors, allowedOptions)),
                MenuOption.of("Crear un nuevo Color"                , this::addNewColor)
        );

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateColorName(List<Color> colors, List<Integer> allowedOptions) {
        int     colorIdx    = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color a editar: ");
        String  newName     = InputUtils.readString("Nuevo nombre: ");

        try {
            colorService.updateColorName(colors.get(colorIdx - 1).getColorId(), newName);
            System.out.println("Nombre del color actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el nombre del color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateColorDesc(List<Color> colors, List<Integer> allowedOptions) {
        int     colorIdx    = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color a editar: ");
        String  newDesc     = InputUtils.readString("Nueva Descripción: ");

        try {
            colorService.updateColorDesc(colors.get(colorIdx - 1).getColorId(), newDesc);
            System.out.println("Descripción del color actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la Descripción del color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateColorCode(List<Color> colors, List<Integer> allowedOptions) {
        int     colorIdx    = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color a editar: ");
        String  newCode     = InputUtils.readString("Nuevo Código: ");

        try {
            colorService.updateColorCode(colors.get(colorIdx - 1).getColorId(), newCode);
            System.out.println("Código del color actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el Código del color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateColorStatus(List<Color> colors, List<Integer> allowedOptions) {
        int colorIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color a editar: ");

        try {
            colorService.updateColorStatus(
                    colors.get(colorIdx - 1).getColorId(),
                    InputUtils.readRowStatus()
            );
            System.out.println("Estado del color actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el Estado del color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void addNewColor() {
        MenuHelper.printMenuTitle("FORMULARIO INICIO DE CREACIÓN DE COLOR:");
        String colorName = InputUtils.readString("Nombre del color: ");
        String colorDesc = InputUtils.readString("Descripción del color: ");
        String colorCode = InputUtils.readString("Código del color: ");

        try {
            colorService.createColor(colorName, colorDesc, colorCode);
        } catch (Exception e) {
            System.err.println("Error al crear nuevo color: " + e.getMessage());
        }

        renderAdminColors();
    }

    private void renderAdminProducts() {
        List<Product> products = productService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE PRODUCTOS", products, true);

        if (allowedOptions.isEmpty()) render();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar la Categoría de un Producto"          , this::updateProductCategory),
                MenuOption.of("Editar el Nombre de un Producto"             , () -> updateProductName(products, allowedOptions)),
                MenuOption.of("Editar la Descripción Corta de un Producto"  , () -> updateProductShortDesc(products, allowedOptions)),
                MenuOption.of("Editar la Descripción Larga de un Producto"  , () -> updateProductLongDesc(products, allowedOptions)),
                MenuOption.of("Cambiar el Estado de un Producto"            , () -> updateProductStatus(products, allowedOptions)),
                MenuOption.of("Gestionar Variantes de un Producto"          , () -> renderAdminVariants(products, allowedOptions)),
                MenuOption.of("Crear un nuevo Producto"                     , this::addNewProduct)
        );

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateProductCategory(){
        System.err.println("FUNCIONALIDAD PARA EDITAR CATEGORÍA NO DISPONIBLE");
        renderAdminProducts();
    }
    private void updateProductName(List<Product> products, List<Integer> allowedOptions) {
        int     idx     = AuxiliarFunction.requireUserOption(allowedOptions, "Número del Producto a editar: ");
        String  newName = InputUtils.readString("Nuevo nombre: ");

        try {
            productService.updateName(products.get(idx - 1).getProductId(), newName);
            System.out.println("Nombre del producto actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el nombre del producto: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateProductShortDesc(List<Product> products, List<Integer> allowedOptions) {
        int     idx     = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto a editar: ");
        String  newDesc = InputUtils.readString("Nueva Descripción Corta: ");

        try {
            productService.updateShortDesc(products.get(idx - 1).getProductId(), newDesc);
            System.out.println("Descripción Corta del producto actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la Descripción Corta del producto: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateProductLongDesc(List<Product> products, List<Integer> allowedOptions) {
        int     idx     = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto a editar: ");
        String  newDesc = InputUtils.readString("Nueva Descripción Larga: ");

        try {
            productService.updateLongDesc(products.get(idx - 1).getProductId(), newDesc);
            System.out.println("Descripción Larga del producto actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la Descripción Larga del producto: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void updateProductStatus(List<Product> products, List<Integer> allowedOptions) {
        int idx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto a editar: ");

        try {
            productService.updateStatus(
                    products.get(idx - 1).getProductId(),
                    InputUtils.readRowStatus()
            );
            System.out.println("Estado del producto actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el Estado del producto: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void addNewProduct() {
        MenuHelper.printMenuTitle("FORMULARIO INICIO DE CREACIÓN DE PRODUCTO:");
        String productName      = InputUtils.readString("Nombre del producto: ");
        String productShortDesc = InputUtils.readString("Descripción Corta del producto: ");
        String productLongDesc  = InputUtils.readString("Descripción Larga del producto: ");

        try {
            productService.createProduct(
                    Category.CALZADO,
                    productName,
                    productShortDesc,
                    productLongDesc
            );
        } catch (Exception e) {
            System.err.println("Error al crear un nuevo producto: " + e.getMessage());
        }

        renderAdminColors();
    }

    private void renderAdminVariants(List<Product> products, List<Integer> allowedProductOptions) {
        int productIdx = AuxiliarFunction.requireUserOption(allowedProductOptions, "Número del producto: ");
        Product product = products.get(productIdx - 1);

        List<ProductVariant> variants = product.getVariants();

        List<Integer> allowedVariantOptions = ListPrinter.renderList("VARIANTES DE " + product.getProductName(), variants, true);

        if (allowedVariantOptions.isEmpty()) renderAdminProducts();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar el Nombre de un Producto"             , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Editar la Categoría de un Producto"          , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Editar la Descripción Corta de un Producto"  , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Editar la Descripción Larga de un Producto"  , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Cambiar el Estado de un Producto"            , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Gestionar Variantes de un Producto"          , () -> renderAdminVariants(products, allowedVariantOptions)),
                MenuOption.of("Crear un nuevo Producto"                     , () -> renderAdminVariants(products, allowedVariantOptions))
        );

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
}