package com.calwillyfiorella;

import com.calwillyfiorella.model.*;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.util.AuxiliarFuncs;
import com.calwillyfiorella.util.MenuHelper;
import com.calwillyfiorella.util.MenuOption;

import java.math.BigDecimal;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Cart cart = new Cart();

    public static final Scanner scanner = new Scanner(System.in);

    private static final List<Color>    colors      = new ArrayList<>();
    private static final List<Size>     sizes       = new ArrayList<>();
    private static final List<Product>  products    = new ArrayList<>();

    public static void main(String[] args) {
        preChrageData();

        renderMainMenu();

        scanner.close();
    }

    public static void renderMainMenu() {
        List<MenuOption> options = List.of(
                MenuOption.of("Ver Manual"      , Main::renderManual),
                MenuOption.of("Administrador"   , Main::renderAdminMenu),
                MenuOption.of("Cliente"         , Main::renderCustomerMenu)
        );

        MenuHelper.renderMenuOptions(options, null);
    }

    private static void renderManual() {
        MenuHelper.printMenuTitle("MANUAL DE SISTEMA");
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
    }

    private static void renderAdminMenu() {
        // Tengo que tener una List con los administradores para poder matchear que el usuario y contraseña que se ingresen sean válidos
        System.out.println("Este es el menú del administrador");
    }

    private static void renderCustomerMenu() {
//        System.out.println("Este es el menú del Cliente");
        /*
            1. Iniciar sesión
            2. Crear una cuenta
         */
        List<MenuOption> options = List.of(
                MenuOption.of("Ver productos"   , Main::renderProductMenu),
                MenuOption.of("Ver carrito"     , Main::renderProductCartMenu)
        );

        MenuHelper.renderMenuOptions(options, Main::renderMainMenu);
    }

    private static void renderProductMenu() {
        List<Integer> allowedOptions = toListProducts();

        List<MenuOption> options = List.of(
                MenuOption.of("Ver variantes de un producto", () -> Main.renderProductVariantMenu(allowedOptions))
        );

        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
    }

    private static void renderProductVariantMenu(List<Integer> allowedProductOptions) {
        int productIdx = AuxiliarFuncs.requireUserOption(allowedProductOptions, "Ingresar el número del producto cuyas variantes quiere ver: ");

        List<ProductVariant> variants = products.get(productIdx - 1).getVariants();

        List<Integer> allowedVariantOptions = toListProductVariants(variants);

        List<MenuOption> options = List.of(
                MenuOption.of("Agregar una variante al carrito de compras", () -> Main.renderAddToCartMenu(variants, allowedVariantOptions))
        );

        MenuHelper.renderMenuOptions(options, Main::renderProductMenu);
    }

    private static void renderAddToCartMenu(List<ProductVariant> availableVariants, List<Integer> allowedVariantOptions) {
        int variantIdx  = AuxiliarFuncs.requireUserOption(allowedVariantOptions, "Ingresar el número de la variante que quiere añadir al carrito: ");
        ProductVariant variant = availableVariants.get(variantIdx - 1);

        boolean done = false;
        while (!done) {
            int quantity = AuxiliarFuncs.readInt("Ingrese la cantidad de unidades de la variante que quiere añadir al carrito: ");

            try {
                cart.addItem(Auth.getActualUser(), variant, quantity);
                System.out.println("La variante fue añadida al carrito con éxito!");
            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }

            done = true;
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Agregar otro producto al carrito de compras" , Main::renderProductMenu),
                MenuOption.of("Ver carrito de compras"                      , Main::renderProductCartMenu)
        );

        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
    }

    private static void renderProductCartMenu() {
        List<CartItem> userCart = toListCartProducts();

        if (userCart.isEmpty()) renderCustomerMenu();

        List<MenuOption> options = List.of(
                MenuOption.of("Modificar la cantidad de un Item", () -> Main.renderProductCartModifiedQuantity(userCart)),
                MenuOption.of("Eliminar un Item"                , () -> Main.renderProductCartMRemoveItem(userCart)),
                MenuOption.of("Vaciar carrito de compras"       , () -> {
                    cart.clearUserItems(Auth.getActualUser());
                    Main.renderProductCartMenu();
                }),
                MenuOption.of("Iniciar proceso de compra"       , () -> Main.renderProductCartStartBuying(userCart))
        );

        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
    }

    private static void renderProductCartModifiedQuantity(List<CartItem> userCart) {
        int idx = AuxiliarFuncs.requireUserOption(userCart.size(), "Ingresar el número del item cuya cantidad desea modificar: ");

        CartItem cartItem = userCart.get(idx - 1);

        int quantity = 0;

        boolean done = false;
        while (!done) {
            quantity = AuxiliarFuncs.readInt("Ingrese la nueva cantidad que desa asignar: ");

            try {
                cart.updateQuantity(Auth.getActualUser(), cartItem.getVariant(), quantity);
                System.out.println("La cantidad fue modificada con éxito!");
            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }

            done = true;
        }
        renderProductCartMenu();
    }

    private static void renderProductCartMRemoveItem(List<CartItem> userCart) {
        userCart.remove(
                AuxiliarFuncs.requireUserOption(
                        userCart.size(), "Ingresar el número del item que desea eliminar del carrito: "
                ) - 1
        );
        renderProductCartMenu();
    }

    private static void renderProductCartStartBuying(List<CartItem> userCart) {
//        MenuHelper.renderMenuOptions(options, Main::renderProductCartMenu);
    }

    public static void toListColors() {
        MenuHelper.printMenuTitle("LISTADO DE COLORES");
        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);

            if (!Auth.getActualUser().isAdmin() && color.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format("Nro %d. %s%n", i + 1, color);
        }
    }

    public static void toListSizes() {
        MenuHelper.printMenuTitle("LISTADO DE TALLES");
        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);

            if (!Auth.getActualUser().isAdmin() && size.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format("Nro %d. %s%n", i + 1, size);
        }
    }

    public static List<Integer> toListProducts() {
        List<Integer> displayedIndexes = new ArrayList<>();

        MenuHelper.printMenuTitle("LISTADO DE PRODUCTOS");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int idx = i + 1;

            if (!Auth.getActualUser().isAdmin() && product.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format("Producto %d. %s%n", idx, product );

            displayedIndexes.add(idx);
        }

        return displayedIndexes;
    }

    public static List<Integer> toListProductVariants(List<ProductVariant> variants) {
        List<Integer> displayedIndexes = new ArrayList<>();

        MenuHelper.printMenuTitle("LISTADO DE VARIANTES");
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant variant = variants.get(i);
            int idx = i + 1;

            if (!Auth.getActualUser().isAdmin() && variant.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format("Variante %d. %s%n", idx, variant);

            displayedIndexes.add(idx);
        }

        return displayedIndexes;
    }

    public static List<CartItem> toListCartProducts() {
        List<CartItem> userCart = cart.getUserItems(Auth.getActualUser());

        MenuHelper.printMenuTitle("CARRITO DE COMPRAS");

        if (userCart.isEmpty()) {
            System.out.println("EL CARRITO DE COMPRAS SE ENCUENTRA VACÍO");
        } else {
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (int i = 0; i < userCart.size(); i++) {
                CartItem cartItem = userCart.get(i);
                int idx = i + 1;

                System.out.format("Item %d. %s%n", idx, cartItem);

                totalAmount = totalAmount.add(cartItem
                        .getVariant()
                        .getVariantPrice()
                        .multiply(
                                BigDecimal.valueOf(cartItem.getQuantity())
                        )
                );
            }

            System.out.format("""
                -----------------------------
                Total por la compra: $%.2f.-
                -----------------------------
                """,
                    totalAmount
            );
        }

        return userCart;
    }

    /*
        Esta función la cree para simular el inicio de la aplicación con una carga de datos que provendría desde la db
        Crea los objetos mínimos necesarios para una demostración de flujo del sistema y los almacena en una lista
     */
    private static void preChrageData() {
        /*
            Creación de usuarios
         */
        // Como para crear un usuario administrador necesito otro usuario administrador, creo uno ficticio
        Auth.createUser(
                new Users(UserRole.ADMIN,
                        "admin",
                        "Administrador",
                        "admin@admin.com",
                        null,
                        null
                ),
                UserRole.ADMIN,
                "admin",
                "Administrador",
                "admin@admin.com",
                null,
                null
        );

        Auth.createUser(
                null,
                UserRole.CUSTOMER,
                "cust",
                "Customer",
                "cust@cust.com",
                null,
                null
        );

        Auth.userLogin("admin@admin.com", "admin");

        /*
            Creación de objetos de la clase Color
         */
        colors.addAll(
                List.of(
                        new Color(
                                "white",
                                "white as the snow",
                                "#ffffff"
                        ),
                        new Color(
                                "black",
                                "black as the night",
                                "#000000"
                        )
                )
        );

        /*
            Creación de objetos de la clase Size
         */
        sizes.addAll(
                List.of(
                        new Size(
                                "m",
                                "medium",
                                1
                        ),
                        new Size(
                                "l",
                                "large",
                                2
                        )
                )
        );

        /*
            Creación de objetos de la clase Product
         */
        products.addAll(
                List.of(
                        new Product(
                                Category.CALZADO,
                                "Artículo_1",
                                "Esta es la descripción corta del Artículo_1",
                                "Esta es la descripción larga del Artículo_1"
                        ),
                        new Product(
                                Category.CALZADO,
                                "Artículo_2",
                                "Esta es la descripción corta del Artículo_2",
                                "Esta es la descripción larga del Artículo_2"
                        )
                )
        );

        /*
            Creación de objetos de la clase ProductVariant
         */
        ProductVariant var1 = products.getFirst().addVariant(
                colors.getFirst(),
                sizes.getFirst(),
                TargetGender.NINIOS,
                "Descripción de la variante var1 del Artículo_1",
                "SKU-ART-1-VAR-1",
                new BigDecimal("10000"),
                3
        );

        ProductVariant var2 = products.getFirst().addVariant(
                colors.get(1),
                sizes.get(1),
                TargetGender.NINIAS,
                "Descripción de la variante var2 del Artículo_1",
                "SKU-ART-1-VAR-2",
                new BigDecimal("11000"),
                3
        );
        ProductVariant var3 = products.get(1).addVariant(
                colors.getFirst(),
                sizes.getFirst(),
                TargetGender.HOMBRES,
                "Descripción de la variante var3 del Artículo_2",
                "SKU-ART-2-VAR-3",
                new BigDecimal("12000"),
                3
        );
        ProductVariant var4 = products.get(1).addVariant(
                colors.get(1),
                sizes.get(1),
                TargetGender.MUJERES,
                "Descripción de la variante var4 del Artículo_2",
                "SKU-ART-2-VAR-4",
                new BigDecimal("13000"),
                3
        );

        /*
            Creación de objetos de la clase VariantImage
         */
        // Variante 1
        var1.addImage("https://www.prueba_imagen_1.com");
        var1.addImage("https://www.prueba_imagen_2.com");
        // Variante 2
        var2.addImage("https://www.prueba_imagen_3.com");
        var2.addImage("https://www.prueba_imagen_4.com");
        // Variante 3
        var3.addImage("https://www.prueba_imagen_5.com");
        var3.addImage("https://www.prueba_imagen_6.com");
        // Variante 4
        var4.addImage("https://www.prueba_imagen_7.com");
        var4.addImage("https://www.prueba_imagen_8.com");
    }
}