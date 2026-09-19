package com.calwillyfiorella;

import com.calwillyfiorella.model.*;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.model.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static final Scanner scanner = new Scanner(System.in);

    static List<Color>          colors      = new ArrayList<>();
    static List<Size>           sizes       = new ArrayList<>();
    static List<Product>        products    = new ArrayList<>();

    static List<Color>          availableColors     = new ArrayList<>();
    static List<Size>           availableSizes      = new ArrayList<>();
    static List<Product>        availableProducts   = new ArrayList<>();

    static List<CartItem> cart = new ArrayList<>();

    static List<Users>  users = new ArrayList<>();

    static Users actualUser = null;

    public static void main(String[] args) {
        preChrageData();


        renderMainMenu();


        scanner.close();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Error: Debes ingresar un número entero válido.");
            }
        }
    }

    private static int requireUserOption(Integer... allowedOptions) {
        Set<Integer> validOptions = Set.of(allowedOptions);
        while (true) {
            int option = readInt("Ingresar opción: ");
            if (!validOptions.contains(option)) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option; // Retorno directo, sin necesidad del booleano 'exit'
        }
    }

    private static void renderMainMenu() {
        System.out.format("""
                            Ingrese el número de la opción deseada:
                                1. Administrador.
                                2. Cliente.
                                3. Finalizar.
                            """);

        switch (requireUserOption(1, 2, 3)) {
            case 1  -> renderAdminMenu();
            case 2  -> renderCustomerMenu();
            case 3  -> System.out.println("Gracias por utilizar nuestro sistema!");
            default -> thereWasAnUnexpectedError();
        }
    }

    private static void renderAdminMenu() {
        // Tengo que tener una List con los administradores para poder matchear que el usuario y contraseña que se ingresen sean válidos
        System.out.println("Este es el menú del administrador");
    }

    private static void renderCustomerMenu() {
//        System.out.println("Este es el menú del Cliente");
        /*
            1. Identificars
            2. Crear una cuenta
            3. Continuar sin identificarse
         */
        System.out.format("""
                            Ingrese el número de la opción deseada:
                                1. Ver productos.
                                2. Ver carrito.
                                3. Volver al menú anterior.
                                4. Finalizar.
                            """);

        switch (requireUserOption(1, 2, 3, 4)) {
            case 1  -> renderProductMenu();
            case 2  -> renderProductCart();
            case 3  -> renderMainMenu();
            case 4  -> System.out.println("Gracias por utilizar nuestro sistema!");
            default -> thereWasAnUnexpectedError();
        }
    }

    private static void renderProductMenu() {
        toListAAvailableProducts();

        System.out.format("""
                            Ingrese el número de la opción deseada:
                                1. Ver variantes de un producto.
                                2. Volver al menú anterior.
                                3. Finalizar.
                            """);

        switch (requireUserOption(1, 2, 3, 4)) {
            case 1  -> renderProductVariantMenu();
            case 2  -> renderCustomerMenu();
            case 3  -> System.out.println("Gracias por utilizar nuestro sistema!");
            default -> thereWasAnUnexpectedError();
        }
    }

    private static void renderProductVariantMenu() {
        int productIdx = 0;
        boolean done = false;

        while (!done) {
            productIdx = readInt("Ingresar el número del producto cuyas variantes desea ver: ") - 1;
            if (productIdx < 0 || productIdx >= products.size()) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            done = true;
        }

        List<ProductVariant> availableVariants = availableProducts.get(productIdx).getAvailableVariants();

        System.out.println("#### LISTADO DE VARIANTES DISPONIBLES ####");
        for (int i = 0; i < availableVariants.size(); i++) {
            ProductVariant variant = availableVariants.get(i);
            System.out.println((i + 1) + ". " + variant);
            variant.toListAvailableImages();
        }

        System.out.format("""
                            Ingrese el número de la opción deseada:
                                1. Agregar una variante al carrito de compras.
                                2. Volver al menú anterior.
                                3. Finalizar.
                            """);

        switch (requireUserOption(1, 2, 3)) {
            case 1  -> renderAddToCartMenu(availableVariants);
            case 2  -> renderCustomerMenu();
            case 3  -> System.out.println("Gracias por utilizar nuestro sistema!");
            default -> thereWasAnUnexpectedError();
        }
    }

    private static void renderAddToCartMenu(List<ProductVariant> availableVariants) {
        int     variantIdx  = 0;
        int     quantity        = 0;
        boolean done        = false;

        while (!done) {
            variantIdx = readInt("Ingresar el número de la variante que quiere añadir al carrito: ") - 1;
            if (variantIdx < 0 || variantIdx >= products.size()) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            done = true;
        }

        ProductVariant variant = availableVariants.get(variantIdx);

        done = false;
        while (!done) {
            quantity = readInt("Ingrese la cantidad de unidades de la variante que quiere añadir al carrito: ");
            if (quantity <= 0 || quantity > variant.getVariantStock()) {
                if (quantity <= 0) System.err.println("El valor ingresado debe ser mayor a cero!");
                if (quantity > variant.getVariantStock()) System.err.println("La cantidad ingresada supera el stock disponible!");
                continue;
            }
            done = true;
        }

        addToCart(variant, quantity);

        renderMainMenu();
    }

    private static void addToCart(ProductVariant variant, Integer quantity) {
        cart.add(new CartItem(actualUser, variant, quantity));
    }

    private static void thereWasAnUnexpectedError() {
        System.err.println("Hubo un error inesperado.");
        renderMainMenu();
    }

    private static void renderProductCart() {
        System.out.println("#### CARRITO DE COMPRAS ####");
        for (int i = 0; i < cart.size(); i++) {
            System.out.println((i + 1) + ". " + cart.get(i));
        }
    }

    public static void toListAllColors() {
        System.out.println("#### LISTADO COMPLETO DE COLORES ####");
        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);
            System.out.format(
                    "%d. Color: %s - Código: %s - Estado: %s%n",
                    i + 1,
                    color.getColorName(),
                    color.getColorCode(),
                    color.getRowStatus()
            );
        }
    }

    private static void updateAvailableColors() {
        availableColors = colors.stream().filter(c -> c.getRowStatus() == RowStatus.ACTIVE).toList();
    }

    public static void toListAAvailableColors() {
        updateAvailableColors();
        System.out.println("#### LISTADO DE COLORES DISPONIBLES ####");
        for (int i = 0; i < availableColors.size(); i++) {
            Color color = availableColors.get(i);
            System.out.format(
                    "%d. Color: %s - Código: %s%n",
                    i + 1,
                    color.getColorName(),
                    color.getColorCode()
            );
        }
    }

    public static void toListAllSizes() {
        System.out.println("#### LISTADO COMPLETO DE TALLES ####");
        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            System.out.format(
                    "%d. Talle: %s - Descripción: %s - Estado: %s%n",
                    i + 1,
                    size.getSize(),
                    size.getSizeDesc(),
                    size.getRowStatus()
            );
        }
    }

    private static void updateAvailableSizes() {
        availableSizes = sizes.stream().filter(s -> s.getRowStatus() == RowStatus.ACTIVE).toList();
    }

    public static void toListAAvailableSizes() {
        updateAvailableSizes();
        System.out.println("#### LISTADO DE TALLES DISPONIBLES ####");
        for (int i = 0; i < availableSizes.size(); i++) {
            Size size = availableSizes.get(i);
            System.out.format(
                    "%d. Talle: %s - Descripción: %s%n",
                    i + 1,
                    size.getSize(),
                    size.getSizeDesc()
            );
        }
    }

    public static void toListAllProducts() {
        System.out.println("#### LISTADO COMPLETO DE PRODUCTOS ####");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.format(
                    "%d. Producto: %s - Total Variantes: %d - Variantes Activas: %d - Estado: %s%n",
                    i + 1,
                    product.getProductName(),
                    product.getTotalVariantsAmount(),
                    product.getAvailableVariantsAmount(),
                    product.getRowStatus()
            );
        }
    }

    private static void updateAvailableProducts() {
        availableProducts = products.stream().filter(p -> p.getRowStatus() == RowStatus.ACTIVE).toList();
    }

    public static void toListAAvailableProducts() {
        updateAvailableProducts();
        System.out.println("#### LISTADO DE PRODUCTOS DISPONIBLES ####");
        for (int i = 0; i < availableProducts.size(); i++) {
            Product product = availableProducts.get(i);
            System.out.format(
                    "%d. Producto: %s - Variantes: %d%n",
                    i + 1,
                    product.getProductName(),
                    product.getAvailableVariantsAmount()
            );
        }
    }

    /*
        Esta función la cree para simular el inicio de la aplicación con una carga de datos que provendría desde la db
        Crea los objetos mínimos necesarios para una demostración de flujo del sistema y los almacena en una lista
     */
    private static void preChrageData() {
        /*
            Creación de objetos de la clase Color
         */
        Color col1 = new Color(
                1,
                "white",
                "white as the snow",
                "#ffffff",
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
        Color col2 = new Color(
                2,
                "black",
                "black as the night",
                "#000000",
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );

        /*
            Creación de objetos de la clase Size
         */
        Size size1 = new Size(
                1,
                "m",
                "medium",
                1,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
        Size size2 = new Size(
                2,
                "l",
                "large",
                2,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );

        /*
            Creación de objetos de la clase Product
         */
        Product prod1 = new Product(
                UUID.randomUUID(),
                Category.CALZADO,
                "Artículo_1",
                "Esta es la descripción corta del Artículo_1",
                "Esta es la descripción larga del Artículo_1",
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
        Product prod2 = new Product(
                UUID.randomUUID(),
                Category.CALZADO,
                "Artículo_2",
                "Esta es la descripción corta del Artículo_2",
                "Esta es la descripción larga del Artículo_2",
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );

        /*
            Creación de objetos de la clase ProductVariant
         */
        ProductVariant var1 = prod1.addVariant(
                col1,
                size1,
                TargetGender.NINIOS,
                "Descripción de la variante var1 del Artículo_1",
                "SKU-ART-1-VAR-1",
                new BigDecimal("10000"),
                3
        );
        ProductVariant var2 = prod1.addVariant(
                col2,
                size2,
                TargetGender.NINIAS,
                "Descripción de la variante var2 del Artículo_1",
                "SKU-ART-1-VAR-2",
                new BigDecimal("11000"),
                3
        );
        ProductVariant var3 = prod2.addVariant(
                col1,
                size1,
                TargetGender.HOMBRES,
                "Descripción de la variante var3 del Artículo_2",
                "SKU-ART-2-VAR-3",
                new BigDecimal("12000"),
                3
        );
        ProductVariant var4 = prod2.addVariant(
                col2,
                size2,
                TargetGender.MUJERES,
                "Descripción de la variante var4 del Artículo_2",
                "SKU-ART-2-VAR-4",
                new BigDecimal("13000"),
                3
        );

        /*
            Creación de usuarios
         */
        users.addAll(List.of(
                new Users(UserRole.ADMIN, "admin", "Administrador", "admin@admin.com", null, null),
                new Users(UserRole.CUSTOMER, "cust", "Customer", "cust@cust.com", null, null)
        ));

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

        /*
            Carga de todas las instancias creadas a las listas estáticas del main
         */
        colors  .addAll(List.of(col1, col2));
        sizes   .addAll(List.of(size1, size2));
        products.addAll(List.of(prod1, prod2));

        /*
            Carga de todas las instancias con RowStatus.ACTIVE a las listas estáticas del main
         */
        availableColors     .addAll(colors.stream().filter(col -> col.getRowStatus() == RowStatus.ACTIVE).toList());
        availableSizes      .addAll(sizes.stream().filter(size -> size.getRowStatus() == RowStatus.ACTIVE).toList());
        availableProducts   .addAll(products.stream().filter(prod -> prod.getRowStatus() == RowStatus.ACTIVE).toList());
    }
}