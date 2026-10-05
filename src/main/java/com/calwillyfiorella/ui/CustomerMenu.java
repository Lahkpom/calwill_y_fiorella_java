package com.calwillyfiorella.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.calwillyfiorella.model.Cart;
import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Sale;
import com.calwillyfiorella.model.SaleItem;
import com.calwillyfiorella.model.enums.PaymentMethod;
import com.calwillyfiorella.model.enums.PaymentStatus;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.SaleStatus;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.CartService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.service.SaleService;
import com.calwillyfiorella.ui.utils.ListPrinter;
import com.calwillyfiorella.ui.utils.MenuHelper;
import com.calwillyfiorella.ui.utils.MenuOption;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;

public class CustomerMenu {
    private final ProductService    productService;
    private final CartService       cartService;
    private final SaleService       saleService;
    private final AuthMenu          authMenu;
    private final MenuHelper        menuHelper;
    private final Runnable          mainMenu;

    public CustomerMenu(
            ProductService  productService,
            CartService     cartService,
            SaleService     saleService,
            AuthMenu        authMenu,
            MenuHelper      menuHelper,
            Runnable        mainMenu
    ) {
        this.productService = Objects.requireNonNull(productService);
        this.cartService    = Objects.requireNonNull(cartService);
        this.saleService    = Objects.requireNonNull(saleService);
        this.authMenu       = Objects.requireNonNull(authMenu);
        this.menuHelper     = Objects.requireNonNull(menuHelper);
        this.mainMenu       = Objects.requireNonNull(mainMenu);
    }

    public void render() {
        if (AuthService.getActualUser() == null) {
            authMenu.render(false, mainMenu);
            if (AuthService.getActualUser() == null) return;
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Ver mi información"  , () -> authMenu.renderUserInfo(this::render)),
                MenuOption.of("Ver Productos"       , this::renderProducts),
                MenuOption.of("Ver mi Carrito"      , this::renderCartItems),
                MenuOption.of("Ver mis Compras"     , this::renderMySales)
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderProducts() {
        List<Product> products = productService.getAll();

        List<Integer> allowedProductOptions = ListPrinter.renderList("CATÁLOGO DE PRODUCTOS", products, false);

        if (allowedProductOptions.isEmpty()) render();

        List<MenuOption> options = List.of(
                MenuOption.of("Ver variantes de un producto", () -> renderProductVariants(products, allowedProductOptions))
        );

        menuHelper.renderMenuOptions(options, this::render);
    }

    private void renderProductVariants(List<Product> products, List<Integer> allowedProductOptions) {
        int idx = AuxiliarFunction.requireUserOption(allowedProductOptions, "Número del producto: ", true);

        try {
            List<ProductVariant> variants = productService.getProduct(products.get(idx - 1).getId()).findAllVariants();

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
        Cart userCart = cartService.getUserCart(AuthService.getActualUser());
        MenuHelper.printMenuTitle("CARRITO DE COMPRAS");

        if (userCart.isEmpty()) {
            System.out.println("El carrito está vacío.");
            render();
            return;
        }

        for (int i = 0; i < userCart.getItems().size(); i++) {
            CartItem item = userCart.getItems().get(i);
            System.out.printf("Item %d. %s%n", i + 1, item);
        }
        System.out.printf("Total: $%.2f%n", userCart.getTotal());

        List<MenuOption> options = List.of(
                MenuOption.of("Modificar la cantidad de un Item", () -> this.updateQuantity(userCart)),
                MenuOption.of("Eliminar un Item"                , () -> this.removeCartItem(userCart)),
                MenuOption.of("Vaciar carrito"                  , this::clearCart),
                MenuOption.of("Iniciar compra"                  , this::renderProductCartStartBuying)
        );
        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateQuantity(Cart userCart) {
        int idx         = AuxiliarFunction.requireUserOption(userCart.getItems().size(), "Ingresar el número del item cuya cantidad desea modificar: ", true);
        int quantity    = InputUtils.readInt("Ingrese la nueva cantidad que desa asignar: ", true);

        try {
            CartItem item = userCart.getItems().get(idx - 1);
            cartService.updateQuantity(AuthService.getActualUser(), item.getVariant(), quantity);
            System.out.println("La cantidad fue modificada con éxito!");
        } catch (Exception e) {
            System.err.println("Error al modificar la cantidad del Item: " + e.getMessage());
        }

        renderCartItems();
    }
    private void removeCartItem(Cart userCart) {
        int idx = AuxiliarFunction.requireUserOption(userCart.getItems().size(), "Ingresar el número del item que desea eliminar del carrito: ", true);

        try {
            CartItem item = userCart.getItems().get(idx - 1);
            cartService.removeItem(AuthService.getActualUser(), item.getVariant());
        } catch (Exception e) {
            System.err.println("Error al eliminar Item del carrito: " + e.getMessage());
        }

        renderCartItems();
    }
    private void clearCart() {
        cartService.clearUserItems(AuthService.getActualUser());
        renderCartItems();
    }

    private void renderProductCartStartBuying() {
        Cart cart = cartService.getUserCart(AuthService.getActualUser());
        if (cart.isEmpty()) {
            System.out.println("El carrito está vacío.");
            render();
            return;
        }

        String shippingAddress = InputUtils.readString("Dirección de envío: ", true);
        PaymentMethod paymentMethod = selectPaymentMethod();
        List<SaleItem> saleItems = cart.getItems().stream()
                .map(this::toSaleItem)
                .toList();

        var user = AuthService.getActualUser();
        Sale sale = new Sale();
        sale.setCustomerName(user.getName());
        sale.setCustomerEmail(user.geteMail());
        sale.setCustomerPhone(user.getPhone());
        sale.setShippingAddress(shippingAddress);
        sale.setShippingCost(BigDecimal.ZERO);
        sale.setSaleStatus(SaleStatus.PENDIENTE);
        sale.setPaymentMethod(paymentMethod);
        sale.setPaymentStatus(PaymentStatus.PENDIENTE);

        try {
            saleService.createSale(sale, user, saleItems);
            cartService.clearUserItems(user);
            System.out.println("¡Compra iniciada correctamente!");
            renderMySales();
        } catch (Exception e) {
            System.err.println("Error al iniciar la compra: " + e.getMessage());
            renderCartItems();
        }
    }

    private SaleItem toSaleItem(CartItem item) {
        ProductVariant variant = item.getVariant();
        return new SaleItem(
                null,
                null,
                variant,
                variant.getDesc(),
                variant.getProduct().getName(),
                variant.getSku(),
                variant.getPrice(),
                item.getQuantity(),
                null,
                null
        );
    }

    private PaymentMethod selectPaymentMethod() {
        PaymentMethod[] methods = PaymentMethod.values();
        MenuHelper.printMenuTitle("MEDIOS DE PAGO");
        for (int i = 0; i < methods.length; i++) {
            System.out.printf("Opción %d. %s%n", i + 1, methods[i]);
        }
        int selectedOption = AuxiliarFunction.requireUserOption(methods.length, "Medio de pago: ", true);
        return methods[selectedOption - 1];
    }

    private void renderMySales() {
        List<Sale> sales = saleService.getSalesByUserId(AuthService.getActualUser().getId());
        if (sales.isEmpty()) {
            MenuHelper.printMenuTitle("MIS COMPRAS");
            System.out.println("Todavía no tienes compras.");
            render();
            return;
        }

        List<Integer> saleOptions = ListPrinter.renderList("MIS COMPRAS", sales, true);
        List<MenuOption> options = List.of(
                MenuOption.of("Ver detalle de una compra", () -> selectCustomerSale(sales, saleOptions))
        );
        menuHelper.renderMenuOptions(options, this::render);
    }

    private void selectCustomerSale(List<Sale> sales, List<Integer> saleOptions) {
        int selected = AuxiliarFunction.requireUserOption(saleOptions, "Número de la compra: ", true);
        renderCustomerSaleDetails(sales.get(selected - 1));
    }

    private void renderCustomerSaleDetails(Sale sale) {
        MenuHelper.printMenuTitle("DETALLE DE COMPRA");
        System.out.printf("Cliente: %s%nEmail: %s%nTeléfono: %s%n", sale.getCustomerName(), sale.getCustomerEmail(), sale.getCustomerPhone());
        System.out.printf("Dirección: %s%nEstado: %s%nPago: %s (%s)%n", sale.getShippingAddress(), sale.getSaleStatus(), sale.getPaymentStatus(), sale.getPaymentMethod());
        System.out.printf("Subtotal: %s%nEnvío: %s%nTotal: %s%nNotas: %s%n", sale.getSaleSubtotal(), sale.getShippingCost(), sale.getSaleTotal(), sale.getSaleNotes());
        System.out.println("Artículos:");
        sale.findAllItems().forEach(item -> System.out.printf("  - %s%n", item));

        List<MenuOption> options = sale.getRowStatus() == RowStatus.ACTIVE
            && sale.getSaleStatus() != SaleStatus.CANCELADO
                ? List.of(MenuOption.of("Cancelar compra", () -> confirmCustomerSaleCancellation(sale)))
                : List.of();
        menuHelper.renderMenuOptions(options, this::renderMySales);
    }

    private void confirmCustomerSaleCancellation(Sale sale) {
        menuHelper.renderMenuOptions(
                List.of(MenuOption.of("Confirmar cancelación", () -> {
                    try {
                        saleService.cancelSaleByCustomer(sale.getId(), AuthService.getActualUser());
                        System.out.println("La compra fue cancelada.");
                    } catch (Exception e) {
                        System.err.println("No se pudo cancelar la compra: " + e.getMessage());
                    }
                    renderCustomerSaleDetails(sale);
                })),
                () -> renderCustomerSaleDetails(sale)
        );
    }
}