package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.CartService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.ui.viewUtils.ListPrinter;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.ui.menuUtils.*;
import com.calwillyfiorella.util.InputUtils;

import java.math.BigDecimal;
import java.util.List;

public class CustomerMenu {
    private final ProductService    productService;
    private final CartService       cartService;
    private final AuthMenu          authMenu;
    private final MenuHelper        menuHelper;

    public CustomerMenu(
            ProductService  productService,
            CartService     cartService,
            AuthMenu        authMenu,
            MenuHelper      menuHelper
    ) {
        this.productService = productService;
        this.cartService    = cartService;
        this.authMenu       = authMenu;
        this.menuHelper     = menuHelper;
    }

    public void render(Runnable onBack) {
        if (AuthService.getActualUser() == null) {
            authMenu.render(false, onBack);
            if (AuthService.getActualUser() == null) return;
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Ver Productos"   , this::renderProducts),
                MenuOption.of("Ver mi Carrito"  , this::renderCartItems),
                MenuOption.of("Ver mis Compras" , () -> System.out.println("FUNCIÓN EN DESARROLLO"))
        );

        menuHelper.renderMenuOptions(options, onBack);
    }

    private void renderProducts() {
        List<Product> products = productService.getAll();

        List<Integer> allowed = ListPrinter.renderList("CATÁLOGO DE PRODUCTOS", products, false);

        List<MenuOption> options = List.of(
                MenuOption.of("Ver variantes de un producto", () -> renderProductVariants(products, allowed))
        );

        menuHelper.renderMenuOptions(options, () -> render(null));
//        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }

    private void renderProductVariants(List<Product> products, List<Integer> allowed) {
        int idx = AuxiliarFunction.requireUserOption(allowed, "Número del producto: ");

        Product product = products.get(idx - 1);

        List<ProductVariant> variants = product.getVariants();
        List<Integer> allowedVariants = ListPrinter.renderList("VARIANTES DISPONIBLES", variants, false);

        List<MenuOption> options = List.of(
                MenuOption.of("Agregar una variante al carrito", () -> addToCart(variants, allowedVariants))
        );

        menuHelper.renderMenuOptions(options, this::renderProducts);
    }

    private void addToCart(List<ProductVariant> variants, List<Integer> allowedVariants) {
        int variantIdx = AuxiliarFunction.requireUserOption(allowedVariants, "Número de la variante: ");

        ProductVariant variant = variants.get(variantIdx - 1);

        int qty = InputUtils.readInt("Cantidad: ");

        try {
            cartService.addItem(AuthService.getActualUser(), variant, qty);
            System.out.println("¡Variante añadida al carrito!");
        } catch (Exception e) {
            System.err.println("Error al añadir item al carrito: " + e.getMessage());
        }

        renderCartItems();
    }

    private void renderCartItems() {
        List<CartItem> userCart = cartService.getUserItems(AuthService.getActualUser());
        MenuHelper.printMenuTitle("CARRITO DE COMPRAS");

        if (userCart.isEmpty()) {
            System.out.println("El carrito está vacío.");
            render(() -> {});
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < userCart.size(); i++) {
            CartItem item = userCart.get(i);
            System.out.printf("Item %d. %s%n", i + 1, item);
            total = total.add(item.getVariant().getVariantPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        System.out.printf("Total: $%.2f%n", total);

        List<MenuOption> options = List.of(
                MenuOption.of("Vaciar carrito", () -> {
                    cartService.clearUserItems(AuthService.getActualUser());
                    renderCartItems();
                })
        );
        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }
}