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
import java.util.Objects;

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
        this.productService = Objects.requireNonNull(productService);
        this.cartService    = Objects.requireNonNull(cartService);
        this.authMenu       = Objects.requireNonNull(authMenu);
        this.menuHelper     = Objects.requireNonNull(menuHelper);
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

        List<Integer> allowedProductOptions = ListPrinter.renderList("CATÁLOGO DE PRODUCTOS", products, false);

        if (allowedProductOptions.isEmpty()) render(() -> {});

        List<MenuOption> options = List.of(
                MenuOption.of("Ver variantes de un producto", () -> renderProductVariants(products, allowedProductOptions))
        );

        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }

    private void renderProductVariants(List<Product> products, List<Integer> allowedProductOptions) {
        int idx = AuxiliarFunction.requireUserOption(allowedProductOptions, "Número del producto: ", true);

        try {
            List<ProductVariant> variants = productService.getProduct(products.get(idx - 1).getId()).getVariants();

            List<Integer> allowedVariantsOptions = ListPrinter.renderList("VARIANTES DISPONIBLES", variants, false);

            if (allowedVariantsOptions.isEmpty()) renderProducts();

            List<MenuOption> options = List.of(
                    MenuOption.of("Agregar una variante al carrito", () -> addToCart(variants, allowedVariantsOptions))
            );

            menuHelper.renderMenuOptions(options, this::renderProducts);
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de variantes del producto: " + e.getMessage());
            renderProducts();
        }
    }

    private void addToCart(List<ProductVariant> variants, List<Integer> allowedVariantsOptions) {
        int variantIdx = AuxiliarFunction.requireUserOption(allowedVariantsOptions, "Número de la variante: ", true);

        int qty = InputUtils.readInt("Cantidad: ", true);

        try {
            cartService.addItem(AuthService.getActualUser(), variants.get(variantIdx - 1), qty);
            System.out.println("¡Variante añadida al carrito!");
        } catch (Exception e) {
            System.err.println("Error al añadir item al carrito: " + e.getMessage());
        }

        renderProducts();
    }

    private void renderCartItems() {
        List<CartItem> userCart = cartService.getUserItems(AuthService.getActualUser());
        MenuHelper.printMenuTitle("CARRITO DE COMPRAS");

        if (userCart.isEmpty()) {
            System.out.println("El carrito está vacío.");
            render(() -> {});
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < userCart.size(); i++) {
            CartItem item = userCart.get(i);
            System.out.printf("Item %d. %s%n", i + 1, item);
            total = total.add(item.getVariant().getVariantPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        System.out.printf("Total: $%.2f%n", total);

        List<MenuOption> options = List.of(
                MenuOption.of("Modificar la cantidad de un Item", () -> this.updateQuantity(userCart)),
                MenuOption.of("Eliminar un Item"                , () -> this.removeCartItem(userCart)),
                MenuOption.of("Vaciar carrito"                  , this::clearCart),
                MenuOption.of("Iniciar compra"                  , () -> this.renderProductCartStartBuying(userCart))
        );
        menuHelper.renderMenuOptions(options, () -> render(() -> {}));
    }
    private void updateQuantity(List<CartItem> userCart) {
        int idx         = AuxiliarFunction.requireUserOption(userCart.size(), "Ingresar el número del item cuya cantidad desea modificar: ", true);
        int quantity    = InputUtils.readInt("Ingrese la nueva cantidad que desa asignar: ", true);

        try {
            cartService.updateQuantity(userCart.get(idx - 1).getItemId(), quantity);
            System.out.println("La cantidad fue modificada con éxito!");
        } catch (Exception e) {
            System.err.println("Error al modificar la cantidad del Item: " + e.getMessage());
        }

        renderCartItems();
    }
    private void removeCartItem(List<CartItem> userCart) {
        int idx = AuxiliarFunction.requireUserOption(userCart.size(), "Ingresar el número del item que desea eliminar del carrito: ", true);

        try {
            cartService.removeItem(userCart.get(idx - 1).getItemId());
        } catch (Exception e) {
            System.err.println("Error al eliminar Item del carrito: " + e.getMessage());
        }

        renderCartItems();
    }
    private void clearCart() {
        cartService.clearUserItems(AuthService.getActualUser());
        renderCartItems();
    }

    private void renderProductCartStartBuying(List<CartItem> userCart) {
        System.out.println("LA FUNCIÓN DE INICIAR COMPRA AÚN SE ENCUENTRA EN DESARROLLO");
        renderCartItems();
//        MenuHelper.renderMenuOptions(options, Main::renderCartItems);
    }
}