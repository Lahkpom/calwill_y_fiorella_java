package com.calwillyfiorella.ui;

import com.calwillyfiorella.service.*;
import com.calwillyfiorella.ui.menuUtils.*;

import java.util.List;

public class MainMenu {
    private final MenuHelper    menuHelper;
    private final AdminMenu     adminMenu;
    private final CustomerMenu  customerMenu;

    public MainMenu(
            AuthService     authService,
            UserService     userService,
            ProductService  productService,
            ColorService    colorService,
            CartService     cartService
    ) {
        this.menuHelper     = new MenuHelper(authService, this::render);
        AuthMenu authMenu   = new AuthMenu(authService, userService, menuHelper);
        this.adminMenu      = new AdminMenu(productService, colorService, authMenu, menuHelper);
        this.customerMenu   = new CustomerMenu(productService, cartService, authMenu, menuHelper);
    }

    public void render() {
        List<MenuOption> options = List.of(
                MenuOption.of("Ver Manual"      , this::renderManual),
                MenuOption.of("Administrador"   , () -> adminMenu.render(this::render)),
                MenuOption.of("Cliente"         , () -> customerMenu.render(this::render))
        );

        menuHelper.renderMenuOptions(options, null);
    }

    private void renderManual() {
        com.calwillyfiorella.ui.menuUtils.MenuHelper.printMenuTitle("MANUAL DEL SISTEMA");
        System.out.format("""
                -----------------------
                About:
                -----------------------
                - Este sistema es para un e-commerce de venta de calzados.
                - Contempla usuarios administradores y clientes.
                - Permite CRUD de productos y paramétricas.
                - Contempla un flujo de carrito de compras.
                - Permite la compra de productos.
                - Permite la gestión de las compras en curso.
                - Permite ver estado actual de pedidos y ventas históricas.
                - Cada clase posee un campo de estado, el cual puede tomar los valores 'ACTIVO', 'INACTIVO' o 'ELIMINADO'
                    - Solo los Admins pueden modificar el estado de los registros.
                    - No se realizan bajas lógicas a modo te tener una auditoría.
                - La única clase que no sigue el punto anterior es la del carrito de compras. Esos sí se eliminan.
                -----------------------
                Inicio del sistema:
                -----------------------
                - Al iniciar la ejecución del sistema, se crean objetos por defecto para completar las listas de colores, talles, productos, usuarios, variantes e imágenes.
                - Estos buscan emular haber sido precargados desde una base de datos y permiten una primer navegación más fluída.
                -----------------------
                Usuarios:
                -----------------------
                - En esta primera entrega se contemplan dos tipos de usuarios, Admins y Customers.
                - Por defecto se brindan un usuario administrador { eMail: admin@admin.com, password: admin }, y un usuario cliente { eMail: cust@cust.com, password: cust }.
                - El usuario ADMINISTRADOR:
                    - Tiene permitido acceder al menú del CRUD de productos y gestión de ventas.
                    - Crear otros usuarios administradores.
                - El usuario CLIENTE:
                    - Tiene permitido ver solo registros activos, agregar productos a su carrito, ver el estado de sus compras.
                    - Puede crear su propio usuario (Customer por defecto).
                -----------------------
                Productos:
                -----------------------
                - Las reglas de negocio particulares de este sistema son:
                - Los productos en sí son un pilar general, por ejemplo un producto 'Artículo_1' es dueño del nombre, descripción general, y de qué tipo es (actualmente solo existe el tipo 'calzado').
                - Relacionado a estos pilares, tenemos las Variantes. Dónde un producto puede tener distintas variantes, las cuales poseen:
                    - Color.
                    - Talle.
                    - Género objetivo (Verisón femenina o masculina de un mismo producto).
                    - Descripción particular de la variante.
                    - Precio.
                    - Stock.
                - El Main tiene una List<> de productos.
                - Productos tiene una List<> de sus variantes.
                - Cada variante tiene una List<> de sus imagenes.
                -----------------------
                Carrito de Compras:
                -----------------------
                - Cada variante puede ser agregada al carrito.
                - Cada item del carrito es almacenado en una List<> en el Main.
                - Cada item es asociado al usuario que se encuentra logeado.
                - Cada usuario solo puede ver los items de su propio usuario.
                - Cada usuario puede hacer una ABM de sus propios items.
                -----------------------
                Paramétricas:
                -----------------------
                - Hay dos tipos de paramétricas, algunas son fijas a través de enums y otras que son más versátiles tienene sus propias clases.
                - Solo son modificables por los ADMIN.
                - Los CLIENTE solo pueden ver los registros activos.
                - Aquellos que tienen sus propias clases son COLORES y TALLES.
                - Ambos se alojan en una List<> de cada uno en el Main.
                """);
        render();
    }
}