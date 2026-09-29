package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.ui.viewUtils.ListPrinter;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.ui.menuUtils.*;
import com.calwillyfiorella.util.InputUtils;

import java.util.List;

public class AdminMenu {
    private final ProductService    productService;
    private final ColorService      colorService;
    private final AuthMenu          authMenu;
    private final MenuHelper        menuHelper;

    public AdminMenu(
            ProductService  productService,
            ColorService    colorService,
            AuthMenu        authMenu,
            MenuHelper      menuHelper
    ) {
        this.productService = productService;
        this.colorService   = colorService;
        this.authMenu       = authMenu;
        this.menuHelper     = menuHelper;
    }

    public void render(Runnable onBack) {
        if (!AuthService.actualUserIsAdmin()) {
            authMenu.render(true, onBack);
            if (AuthService.getActualUser() == null) return;
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Gestionar Productos"         , this::renderAdminProducts),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Gestionar Ventas"            , () -> System.out.println("FUNCIÓN EN DESARROLLO")),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, onBack);
    }

    private void renderAdminColors() {
        List<Color> colors = colorService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE COLORES", colors, true);

        List<MenuOption> options = List.of(
                MenuOption.of("Editar el Nombre de un Color", () -> updateColorName(colors, allowedOptions))
        );

        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }

    private void updateColorName(List<Color> colors, List<Integer> allowedOptions) {
        int     colorIdx    = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color a editar: ");
        Color   color       = colors.get(colorIdx - 1);
        String  newName     = InputUtils.readString("Nuevo nombre: ");

        try {
            colorService.updateColorName(color, newName);
            System.out.println("Nombre del color actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el nombre del color: " + e.getMessage());
        }
        renderAdminColors();
    }

    private void renderAdminProducts() {
        List<Product> products = productService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE PRODUCTOS", products, true);

        List<MenuOption> options = List.of(
                MenuOption.of("Ver Variantes de un Producto", () -> renderAdminVariants(products, allowedOptions))
        );

        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }

    private void renderAdminVariants(List<Product> products, List<Integer> allowedOptions) {
        int productIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto: ");
        Product product = products.get(productIdx - 1);

        List<ProductVariant> variants = product.getVariants();
        ListPrinter.renderList("VARIANTES DE " + product.getProductName(), variants, true);
    }
}