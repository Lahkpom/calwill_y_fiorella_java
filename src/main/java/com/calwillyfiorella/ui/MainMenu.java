package com.calwillyfiorella.ui;

import java.util.List;

import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.CartService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.service.ProductVariantService;
import com.calwillyfiorella.service.SaleService;
import com.calwillyfiorella.service.UserService;
import com.calwillyfiorella.ui.utils.MenuHelper;
import com.calwillyfiorella.ui.utils.MenuOption;

public class MainMenu {
    private final MenuHelper    menuHelper;
    private final AdminMenu     adminMenu;
    private final CustomerMenu  customerMenu;

    public MainMenu(
            AuthService             authService,
            UserService             userService,
            ProductService          productService,
            ProductVariantService   productVariantService,
            ColorService            colorService,
            CartService             cartService,
            SaleService             saleService
    ) {
        this.menuHelper     = new MenuHelper(authService, this::render);
        AuthMenu authMenu   = new AuthMenu(authService, userService, menuHelper);
        this.adminMenu      = new AdminMenu(productService, colorService, productVariantService, saleService, authMenu, menuHelper, this::render);
        this.customerMenu   = new CustomerMenu(productService, cartService, saleService, authMenu, menuHelper, this::render);
    }

    public void render() {
        List<MenuOption> options = List.of(
                MenuOption.of("Administrador"   , adminMenu::render),
                MenuOption.of("Cliente"         , customerMenu::render),
                MenuOption.of("Ver Manual"      , this::renderManual)
        );

        menuHelper.renderMenuOptions(options, null);
    }

    private void renderManual() {
        MenuHelper.printMenuTitle("GUÍA DEL SISTEMA");
        System.out.format("""
                PROPÓSITO
                Este programa simula una tienda de calzado. Permite consultar productos,
                administrar un carrito, registrar compras y dar seguimiento a las ventas.

                ACCESO
                Al ingresar, se puede elegir el sector de administración o el de clientes.
                Para probar el sistema están disponibles estas cuentas:
                  Administrador: admin@admin.com / admin
                  Cliente:       cust@cust.com / cust

                RECORRIDO DEL CLIENTE
                1. Consultar el catálogo y elegir un producto para ver sus variantes.
                2. Agregar variantes al carrito, cambiar cantidades, quitar artículos o vaciarlo.
                3. Iniciar la compra indicando una dirección de envío y un medio de pago.
                   El sistema registra los artículos y sus precios, calcula los importes
                   y descuenta del stock las unidades compradas.
                4. Consultar las compras propias y ver sus datos, artículos y estados.
                   Una compra activa puede cancelarse desde su detalle.

                TAREAS DEL ADMINISTRADOR
                - Consultar, crear y editar productos.
                - Crear y editar variantes, incluyendo color, talle, público, precio y stock.
                - Consultar y mantener colores.
                - Ver todas las ventas y sus artículos.
                - Actualizar las notas, el estado de la venta y el estado del pago.
                - Crear cuentas de administrador.

                PRODUCTOS Y COMPRAS
                Un producto reúne la información general del calzado. Sus variantes
                representan las opciones disponibles, como color, talle, público, precio
                y stock. Los clientes ven únicamente productos y variantes disponibles.
                Cada cliente accede solo a su carrito y a sus propias compras; el
                administrador puede consultar las ventas de todos los clientes.

                DATOS DE DEMOSTRACIÓN
                Cada inicio crea usuarios, productos, variantes y colores de ejemplo.
                También se cargan compras de muestra y artículos en los carritos para
                facilitar la exploración de ambos recorridos. Los cambios se conservan
                durante la ejecución actual del programa.

                ALCANCE ACTUAL
                La gestión general de usuarios y la administración de imágenes todavía
                no están disponibles. La cuenta de cliente de prueba ya viene creada.
                """);
        render();
    }
}