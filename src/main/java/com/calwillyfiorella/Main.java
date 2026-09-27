package com.calwillyfiorella;

import com.calwillyfiorella.repository.*;
import com.calwillyfiorella.service.*;
import com.calwillyfiorella.ui.MainMenu;
import com.calwillyfiorella.util.DataSeeder;

public class Main {

    public static void main(String[] args) {
        UserRepository      userRepository      = new UserRepository();
        ProductRepository   productRepository   = new ProductRepository();
        ColorRepository     colorRepository     = new ColorRepository();
        CartRepository      cartRepository      = new CartRepository();

        UserService     userService     = new UserService(userRepository);
        AuthService     authService     = new AuthService(userService);
        ProductService  productService  = new ProductService(productRepository);
        ColorService    colorService    = new ColorService(colorRepository);
        CartService     cartService     = new CartService(cartRepository);

        DataSeeder.seed(
                userRepository,
                productRepository,
                colorRepository
        );

        // 4. Inicialización y arranque de la interfaz de usuario
        MainMenu mainMenu = new MainMenu(
                authService,
                userService,
                productService,
                colorService,
                cartService
        );

        mainMenu.render();
    }
}

//package com.calwillyfiorella;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Scanner;
//import java.util.UUID;
//
//import com.calwillyfiorella.model.Auth;
//import com.calwillyfiorella.model.Color;
//import com.calwillyfiorella.model.Product;
//import com.calwillyfiorella.model.ProductVariant;
//import com.calwillyfiorella.model.Size;
//import com.calwillyfiorella.model.Users;
//import com.calwillyfiorella.model.enums.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    public static final Scanner scanner = new Scanner(System.in);

//    private static final Cart cart = new Cart();
//
//    private static final List<Color>    colors      = new ArrayList<>();
//    private static final List<Size>     sizes       = new ArrayList<>();
//    private static final List<Product>  products    = new ArrayList<>();
//
//    public static void main(String[] args) {
//
//        preChargeData();
////        renderMainMenu();
//    }

//    public static void renderMainMenu() {
//        List<MenuOption> options = List.of(
//                MenuOption.of("Ver Manual"      , Main::renderManual),
//                MenuOption.of("Administrador"   , Main::renderAdminMenu),
//                MenuOption.of("Cliente"         , Main::renderCustomerMenu)
//        );
//
//        MenuHelper.renderMenuOptions(options, null);
//    }
//
//    private static void renderAdminMenu() {
//        if (Auth.getActualUser() == null) renderAuthMenu(true);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Gestionar Productos"         , Main::renderAdminProducts),
//                MenuOption.of("Gestionar Colores"           , Main::renderAdminColors),
//                MenuOption.of("Gestionar Ventas"            , Main::renderAdminSales),
//                MenuOption.of("Crear Usuario Administrador" , Main::createAdminUser)
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderMainMenu);
//    }
//
//    private static void createAdminUser() {
//        createAccount(true);
//    }
//
//    private static void renderAdminSales() {
//        System.out.println("LA FUNCIÓN VER VENTAS AÚN SE ENCUENTRA EN DESARROLLO");
//        renderAdminMenu();
//    }
//
//    private static void renderAdminColors() {
//        List<Integer> allowedOptions = toListColors(true);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Editar el Nombre de un Color"       , () -> Main.updateColorName(allowedOptions)),
//                MenuOption.of("Editar la Descripción de un Color"  , () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar el Código de un Color"       , () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar el Estado de un Color"       , () -> Main.renderCustProductVariants(allowedOptions))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
//    }
//
//    private static void updateColorName(List<Integer> allowedOptions) {
//        int colorIdx = AuxiliarFuncs.requireUserOption(allowedOptions, "Ingresar el número del color cuyo nombre desea editar: ");
//        Color color = colors.get(colorIdx - 1);
//
//        String newName = AuxiliarFuncs.readString("Ingrese el nuevo nombre del color: ");
//
//        try {
//            color.setColorName(newName);
//        } catch (Exception e) {
//            System.err.format("Error al cambiar nombre del color: %s%n", e.getMessage());
//        }
//
//        renderAdminColors();
//    }
//
//    private static void renderAdminProducts() {
//        List<Integer> allowedOptions = toListProducts(true);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Editar Categoría de un Producto"        , () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar Nombre de un Producto"           , () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar Descripción Corta de un Producto", () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar Descripción Larga de un Producto", () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Editar Estado de un Producto"           , () -> Main.renderCustProductVariants(allowedOptions)),
//                MenuOption.of("Ver Variantes de un Producto"            , () -> Main.renderAdminProductVariants(allowedOptions))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
//    }
//
//    private static void renderAdminProductVariants(List<Integer> allowedProductOptions) {
//        int productIdx = AuxiliarFuncs.requireUserOption(allowedProductOptions, "Ingresar el número del producto cuyas variantes quiere ver: ");
//
//        List<ProductVariant> variants = products.get(productIdx - 1).getVariants();
//
//        List<Integer> allowedVariantOptions = toListProductVariants(variants, true);
//
//        // TODO: Modificar las opciones del menú
//        List<MenuOption> options = List.of(
//                MenuOption.of("Agregar una variante al carrito de compras", () -> Main.addToCart(variants, allowedVariantOptions))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustProducts);
//    }
//
//    private static void renderCustomerMenu() {
//        if (Auth.getActualUser() == null) renderAuthMenu(false);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Ver Productos"   , Main::renderCustProducts),
//                MenuOption.of("Ver mi Carrito"  , Main::renderCartItems),
//                MenuOption.of("Ver mis Compras" , () -> Main.renderUserPurchase(Auth.getActualUser()))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderMainMenu);
//    }
//
//    private static void renderUserPurchase(Users user) {
//        System.out.println("LA FUNCIÓN VER COMPRAS AÚN SE ENCUENTRA EN DESARROLLO");
//        renderCustomerMenu();
//    }
//
//    private static void renderAuthMenu(boolean isAdmin) {
//        List<MenuOption> options = new ArrayList<>();
//
//        options.add(MenuOption.of("Iniciar Sesión", () -> Main.logIn(isAdmin)));
//        // La opción de Crear Cuenta solo se le muestra a los Clientes.
//        if (!isAdmin)
//            options.add(MenuOption.of("Crear Cuenta", Main::signUp));
//
//        MenuHelper.renderMenuOptions(options, Main::renderMainMenu);
//    }
//
//    private static void logIn(boolean isAdmin) {
//        MenuHelper.printMenuTitle("FORMULARIO INICIO DE SESIÓN");
//
//        String eMail    = AuxiliarFuncs.readString("Ingrese su e-mail: ");
//        String password = AuxiliarFuncs.readString("Ingrese su contraseña: ");
//
//        try {
//            Auth.userLogin(eMail, password, isAdmin);
//        } catch (Exception e) {
//            System.err.println("Error al iniciar sesión: " + e.getMessage());
//        } finally {
//            if (Auth.getActualUser() != null) System.out.format("""
//                    -------------------------
//                    Sesión iniciada con éxito.
//                    Bienvenido %s!!!
//                    -------------------------
//                    """,
//                    Auth.getActualUser().getUserName()
//            );
//
//            if (isAdmin) {
//                renderAdminMenu();
//            } else {
//                renderCustomerMenu();
//            }
//        }
//    }
//
//    // Esta función solo crea funciones para los usuarios normales
//    private static void signUp() {
//        createAccount(false);
//    }
//
//    private static void createAccount(boolean isAdmin) {
//        MenuHelper.printMenuTitle("FORMULARIO CREACIÓN DE CUENTA");
//
//        String userName     = AuxiliarFuncs.readString("Ingrese el Nombre: ");
//        String usereMail    = AuxiliarFuncs.readString("Ingrese el e-Mail: ");
//        String userPassword = AuxiliarFuncs.readString("Ingrese la Contraseña: ");
//
//        try {
//            Auth.createUser(
//                    isAdmin ? Auth.getActualUser() : null,
//                    isAdmin ? UserRole.ADMIN : UserRole.CUSTOMER,
//                    userPassword,
//                    userName,
//                    usereMail,
//                    null,
//                    null
//            );
//        } catch (Exception e) {
//            System.err.println("Error al crear usuario: " + e.getMessage());
//            renderCustomerMenu();
//        }
//        System.out.println("Creación de cuenta exitosa!");
//
//        logIn(isAdmin);
//    }
//
//    private static void renderCustProducts() {
//        List<Integer> allowedOptions = toListProducts(false);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Ver variantes de un producto", () -> Main.renderCustProductVariants(allowedOptions))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
//    }
//
//    private static void renderCustProductVariants(List<Integer> allowedProductOptions) {
//        int productIdx = AuxiliarFuncs.requireUserOption(allowedProductOptions, "Ingresar el número del producto cuyas variantes quiere ver: ");
//
//        List<ProductVariant> variants = products.get(productIdx - 1).getVariants();
//
//        List<Integer> allowedVariantOptions = toListProductVariants(variants, false);
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Agregar una variante al carrito de compras", () -> Main.addToCart(variants, allowedVariantOptions))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustProducts);
//    }
//
//    private static void addToCart(List<ProductVariant> availableVariants, List<Integer> allowedVariantOptions) {
//        int variantIdx  = AuxiliarFuncs.requireUserOption(allowedVariantOptions, "Ingresar el número de la variante que quiere añadir al carrito: ");
//        ProductVariant variant = availableVariants.get(variantIdx - 1);
//
//        boolean done = false;
//        while (!done) {
//            int quantity = AuxiliarFuncs.readInt("Ingrese la cantidad de unidades de la variante que quiere añadir al carrito: ");
//
//            try {
//                cart.addItem(Auth.getActualUser(), variant, quantity);
//                System.out.println("La variante fue añadida al carrito con éxito!");
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//                continue;
//            }
//
//            done = true;
//        }
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Agregar otro producto al carrito de compras" , Main::renderCustProducts),
//                MenuOption.of("Ver carrito de compras"                      , Main::renderCartItems)
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
//    }
//
//    private static void renderCartItems() {
//        List<CartItem> userCart = toListCartProducts();
//
//        if (userCart.isEmpty()) renderCustomerMenu();
//
//        List<MenuOption> options = List.of(
//                MenuOption.of("Modificar la cantidad de un Item", () -> Main.updateCartQuantity(userCart)),
//                MenuOption.of("Eliminar un Item"                , () -> Main.removeCartItem(userCart)),
//                MenuOption.of("Vaciar carrito de compras"       , () -> {
//                    cart.clearUserItems(Auth.getActualUser());
//                    Main.renderCartItems();
//                }),
//                MenuOption.of("Iniciar proceso de compra"       , () -> Main.renderProductCartStartBuying(userCart))
//        );
//
//        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
//    }
//
//    private static void updateCartQuantity(List<CartItem> userCart) {
//        int idx = AuxiliarFuncs.requireUserOption(userCart.size(), "Ingresar el número del item cuya cantidad desea modificar: ");
//
//        CartItem cartItem = userCart.get(idx - 1);
//
//        int quantity = 0;
//
//        boolean done = false;
//        while (!done) {
//            quantity = AuxiliarFuncs.readInt("Ingrese la nueva cantidad que desa asignar: ");
//
//            try {
//                cart.updateQuantity(Auth.getActualUser(), cartItem.getVariant(), quantity);
//                System.out.println("La cantidad fue modificada con éxito!");
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//                continue;
//            }
//
//            done = true;
//        }
//        renderCartItems();
//    }
//
//    private static void removeCartItem(List<CartItem> userCart) {
//        userCart.remove(
//                AuxiliarFuncs.requireUserOption(
//                        userCart.size(), "Ingresar el número del item que desea eliminar del carrito: "
//                ) - 1
//        );
//        renderCartItems();
//    }
//
//    private static void renderProductCartStartBuying(List<CartItem> userCart) {
//        System.out.println("LA FUNCIÓN DE INICIAR COMPRA AÚN SE ENCUENTRA EN DESARROLLO");
//        renderCartItems();
////        MenuHelper.renderMenuOptions(options, Main::renderCartItems);
//    }
//
//    public static List<Integer> toListColors(boolean isAdminPage) {
//        List<Integer> displayedIndexes = new ArrayList<>();
//
//        MenuHelper.printMenuTitle("LISTADO DE COLORES");
//        for (int i = 0; i < colors.size(); i++) {
//            Color color = colors.get(i);
//            int idx = i + 1;
//
//            if (!isAdminPage && color.getRowStatus() != RowStatus.ACTIVE) continue;
//
//            System.out.format("Color %d. %s%n", idx, color);
//            displayedIndexes.add(idx);
//        }
//        return displayedIndexes;
//    }
//
//    public static List<Integer> toListProducts(boolean isAdminPage) {
//        List<Integer> displayedIndexes = new ArrayList<>();
//
//        MenuHelper.printMenuTitle("LISTADO DE PRODUCTOS");
//        for (int i = 0; i < products.size(); i++) {
//            Product product = products.get(i);
//            int idx = i + 1;
//
//            if (!isAdminPage && product.getRowStatus() != RowStatus.ACTIVE) continue;
//
//            System.out.format("Producto %d. %s%n", idx, product );
//
//            displayedIndexes.add(idx);
//        }
//
//        return displayedIndexes;
//    }
//
//    public static List<Integer> toListProductVariants(List<ProductVariant> variants, boolean isAdminPage) {
//        List<Integer> displayedIndexes = new ArrayList<>();
//
//        MenuHelper.printMenuTitle("LISTADO DE VARIANTES");
//        for (int i = 0; i < variants.size(); i++) {
//            ProductVariant variant = variants.get(i);
//            int idx = i + 1;
//
//            if (!isAdminPage && variant.getRowStatus() != RowStatus.ACTIVE) continue;
//
//            System.out.format("Variante %d. %s%n", idx, variant);
//
//            displayedIndexes.add(idx);
//        }
//
//        return displayedIndexes;
//    }
//
//    public static List<CartItem> toListCartProducts() {
//        List<CartItem> userCart = cart.getUserItems(Auth.getActualUser());
//
//        MenuHelper.printMenuTitle("CARRITO DE COMPRAS");
//
//        if (userCart.isEmpty()) {
//            System.out.println("EL CARRITO DE COMPRAS SE ENCUENTRA VACÍO");
//        } else {
//            BigDecimal totalAmount = BigDecimal.ZERO;
//
//            for (int i = 0; i < userCart.size(); i++) {
//                CartItem cartItem = userCart.get(i);
//                int idx = i + 1;
//
//                System.out.format("Item %d. %s%n", idx, cartItem);
//
//                totalAmount = totalAmount.add(cartItem
//                        .getVariant()
//                        .getVariantPrice()
//                        .multiply(
//                                BigDecimal.valueOf(cartItem.getQuantity())
//                        )
//                );
//            }
//
//            System.out.format("""
//                -----------------------------
//                Total por la compra: $%.2f.-
//                -----------------------------
//                """,
//                    totalAmount
//            );
//        }
//
//        return userCart;
//    }

    /*
        Esta función la cree para simular el inicio de la aplicación con una carga de datos que provendría desde la db
        Crea los objetos mínimos necesarios para una demostración de flujo del sistema y los almacena en una lista
     */
//    private static void preChargeData() {
//        /*
//            Creación de usuarios
//         */
//        // Como para crear un usuario administrador necesito otro usuario administrador, creo uno ficticio
//        Auth.createUser(
//                new Users(UserRole.ADMIN,
//                        "admin",
//                        "Administrador",
//                        "admin@admin.com",
//                        null,
//                        null
//                ),
//                UserRole.ADMIN,
//                "admin",
//                "Administrador",
//                "admin@admin.com",
//                null,
//                null
//        );
//
//        Auth.createUser(
//                null,
//                UserRole.CUSTOMER,
//                "cust",
//                "Customer",
//                "cust@cust.com",
//                null,
//                null
//        );
//
////        Auth.userLogin("admin@admin.com", "admin", true);
//
//        /*
//            Creación de objetos de la clase Color
//         */
//        colors.addAll(
//                List.of(
//                        new Color(
//                                "white",
//                                "white as the snow",
//                                "#ffffff"
//                        ),
//                        new Color(
//                                "black",
//                                "black as the night",
//                                "#000000"
//                        )
//                )
//        );
//
//        /*
//            Creación de objetos de la clase Size
//         */
//        sizes.addAll(
//                List.of(
//                        new Size(
//                                "m",
//                                "medium",
//                                1
//                        ),
//                        new Size(
//                                "l",
//                                "large",
//                                2
//                        )
//                )
//        );
//
//        /*
//            Creación de objetos de la clase Product
//         */
//        products.addAll(
//                List.of(
//                        new Product(
//                                Category.CALZADO,
//                                "Artículo_1",
//                                "Esta es la descripción corta del Artículo_1",
//                                "Esta es la descripción larga del Artículo_1"
//                        ),
//                        new Product(
//                                Category.CALZADO,
//                                "Artículo_2",
//                                "Esta es la descripción corta del Artículo_2",
//                                "Esta es la descripción larga del Artículo_2"
//                        ),
//                        new Product(
//                                UUID.randomUUID(),
//                                Category.CALZADO,
//                                "Artículo_Inactivo",
//                                "Esta es la descripción corta del Artículo_Inactivo",
//                                "Esta es la descripción larga del Artículo_Inactivo",
//                                RowStatus.INACTIVE,
//                                LocalDateTime.now(),
//                                null
//                        )
//                )
//        );
//
//        /*
//            Creación de objetos de la clase ProductVariant
//         */
//        ProductVariant var1 = products.getFirst().addVariant(
//                colors.getFirst(),
//                NumberSize.T_18,
//                TargetGender.NINIOS,
//                "Descripción de la variante var1 del Artículo_1",
//                "SKU-ART-1-VAR-1",
//                new BigDecimal("10000"),
//                3
//        );
//
//        ProductVariant var2 = products.getFirst().addVariant(
//                colors.get(1),
//                NumberSize.T_19,
//                TargetGender.NINIAS,
//                "Descripción de la variante var2 del Artículo_1",
//                "SKU-ART-1-VAR-2",
//                new BigDecimal("11000"),
//                3
//        );
//        ProductVariant var3 = products.get(1).addVariant(
//                colors.getFirst(),
//                NumberSize.T_18,
//                TargetGender.HOMBRES,
//                "Descripción de la variante var3 del Artículo_2",
//                "SKU-ART-2-VAR-3",
//                new BigDecimal("12000"),
//                3
//        );
//        ProductVariant var4 = products.get(1).addVariant(
//                colors.get(1),
//                NumberSize.T_19,
//                TargetGender.MUJERES,
//                "Descripción de la variante var4 del Artículo_2",
//                "SKU-ART-2-VAR-4",
//                new BigDecimal("13000"),
//                3
//        );
//
//        /*
//            Creación de objetos de la clase VariantImage
//         */
//        // Variante 1
//        var1.addImage("https://www.prueba_imagen_1.com");
//        var1.addImage("https://www.prueba_imagen_2.com");
//        // Variante 2
//        var2.addImage("https://www.prueba_imagen_3.com");
//        var2.addImage("https://www.prueba_imagen_4.com");
//        // Variante 3
//        var3.addImage("https://www.prueba_imagen_5.com");
//        var3.addImage("https://www.prueba_imagen_6.com");
//        // Variante 4
//        var4.addImage("https://www.prueba_imagen_7.com");
//        var4.addImage("https://www.prueba_imagen_8.com");
//    }

//    private static void renderManual() {
//        MenuHelper.printMenuTitle("MANUAL DE SISTEMA");
//        System.out.format("""
//                -----------------------
//                About:
//                -----------------------
//                - Este sistema es para un e-commerce de venta de calzados.
//                - Contempla usuarios administradores y clientes.
//                - Permite CRUD de productos y paramétricas.
//                - Contempla un flujo de carrito de compras.
//                - Permite la compra de productos.
//                - Permite la gestión de las compras en curso.
//                - Permite ver estado actual de pedidos y ventas históricas.
//                - Cada clase posee un campo de estado, el cual puede tomar los valores 'ACTIVO', 'INACTIVO' o 'ELIMINADO'
//                    - Solo los Admins pueden modificar el estado de los registros.
//                    - No se realizan bajas lógicas a modo te tener una auditoría.
//                - La única clase que no sigue el punto anterior es la del carrito de compras. Esos sí se eliminan.
//                -----------------------
//                Inicio del sistema:
//                -----------------------
//                - Al iniciar la ejecución del sistema, se crean objetos por defecto para completar las listas de colores, talles, productos, usuarios, variantes e imágenes.
//                - Estos buscan emular haber sido precargados desde una base de datos y permiten una primer navegación más fluída.
//                -----------------------
//                Usuarios:
//                -----------------------
//                - En esta primera entrega se contemplan dos tipos de usuarios, Admins y Customers.
//                - Por defecto se brindan un usuario administrador { eMail: admin@admin.com, password: admin }, y un usuario cliente { eMail: cust@cust.com, password: cust }.
//                - El usuario ADMINISTRADOR:
//                    - Tiene permitido acceder al menú del CRUD de productos y gestión de ventas.
//                    - Crear otros usuarios administradores.
//                - El usuario CLIENTE:
//                    - Tiene permitido ver solo registros activos, agregar productos a su carrito, ver el estado de sus compras.
//                    - Puede crear su propio usuario (Customer por defecto).
//                -----------------------
//                Productos:
//                -----------------------
//                - Las reglas de negocio particulares de este sistema son:
//                - Los productos en sí son un pilar general, por ejemplo un producto 'Artículo_1' es dueño del nombre, descripción general, y de qué tipo es (actualmente solo existe el tipo 'calzado').
//                - Relacionado a estos pilares, tenemos las Variantes. Dónde un producto puede tener distintas variantes, las cuales poseen:
//                    - Color.
//                    - Talle.
//                    - Género objetivo (Verisón femenina o masculina de un mismo producto).
//                    - Descripción particular de la variante.
//                    - Precio.
//                    - Stock.
//                - El Main tiene una List<> de productos.
//                - Productos tiene una List<> de sus variantes.
//                - Cada variante tiene una List<> de sus imagenes.
//                -----------------------
//                Carrito de Compras:
//                -----------------------
//                - Cada variante puede ser agregada al carrito.
//                - Cada item del carrito es almacenado en una List<> en el Main.
//                - Cada item es asociado al usuario que se encuentra logeado.
//                - Cada usuario solo puede ver los items de su propio usuario.
//                - Cada usuario puede hacer una ABM de sus propios items.
//                -----------------------
//                Paramétricas:
//                -----------------------
//                - Hay dos tipos de paramétricas, algunas son fijas a través de enums y otras que son más versátiles tienene sus propias clases.
//                - Solo son modificables por los ADMIN.
//                - Los CLIENTE solo pueden ver los registros activos.
//                - Aquellos que tienen sus propias clases son COLORES y TALLES.
//                - Ambos se alojan en una List<> de cada uno en el Main.
//                """);
//        renderMainMenu();
//    }
//}