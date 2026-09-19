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
    private static final List<Users>    users       = new ArrayList<>();

    private static Users actualUser = null;

    public static void main(String[] args) {
        preChrageData();

        renderMainMenu();

        scanner.close();
    }

    public static void renderMainMenu() {
        List<MenuOption> options = List.of(
                MenuOption.of("Administrador"   , Main::renderAdminMenu),
                MenuOption.of("Cliente"         , Main::renderCustomerMenu)
        );

        MenuHelper.renderMenuOptions(options, null);
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
        List<MenuOption> options = List.of(
                MenuOption.of("Ver productos"   , Main::renderProductMenu),
                MenuOption.of("Ver carrito"     , Main::renderProductCart)
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

        int     quantity    = 0;
        boolean done        = false;

        while (!done) {
            quantity = AuxiliarFuncs.readInt("Ingrese la cantidad de unidades de la variante que quiere añadir al carrito: ");

            try {
                cart.addItem(actualUser, variant, quantity);
                System.out.format("La variante fue añadida al carrito con éxito!");
            } catch (Exception e) {
                System.err.format(e.getMessage());
                continue;
            }

            done = true;
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Agregar otro producto al carrito de compras", Main::renderProductMenu)
        );

        MenuHelper.renderMenuOptions(options, Main::renderCustomerMenu);
    }

    private static void renderProductCart() {
        List<CartItem> userCart = cart.getUserItems(actualUser);
        System.out.println("#### CARRITO DE COMPRAS ####");
        for (int i = 0; i < userCart.size(); i++) {
            System.out.println("Variante " + (i + 1) + ". " + userCart.get(i));
        }
    }

    public static void toListColors() {
        MenuHelper.printMenuTitle("LISTADO DE COLORES");
        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);

            if (!actualUser.isAdmin() && color.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format(
                    "Nro %d. Color: %s - Código: %s - Estado: %s%n",
                    i + 1,
                    color.getColorName(),
                    color.getColorCode(),
                    color.getRowStatus()
            );
        }
    }

    public static void toListSizes() {
        MenuHelper.printMenuTitle("LISTADO DE TALLES");
        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);

            if (!actualUser.isAdmin() && size.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format(
                    "Nro %d. Talle: %s - Descripción: %s - Estado: %s%n",
                    i + 1,
                    size.getSize(),
                    size.getSizeDesc(),
                    size.getRowStatus()
            );
        }
    }

    public static List<Integer> toListProducts() {
        List<Integer> displayedIndexes = new ArrayList<>();

        MenuHelper.printMenuTitle("LISTADO DE PRODUCTOS");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int idx = i + 1;

            if (!actualUser.isAdmin() && product.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format(
                    "Producto %d. Nombre: %s - Variantes Activas: %d - Estado: %s%n",
                    idx,
                    product.getProductName(),
                    product.getAvailableVariantsAmount(),
                    product.getRowStatus()
            );
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

            if (!actualUser.isAdmin() && variant.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format(
                    "Variante %d. Producto: %s - Género: %s - Descripción: %s - Color: %s - Talle: %s - Imágenes: %d - Stock: %d - Precio: %.2f - Estado: %s%n",
                    idx,
                    variant.getProduct().getProductName(),
                    variant.getTargetGender(),
                    variant.getVariantDesc(),
                    variant.getColor(),
                    variant.getSize(),
                    variant.getImages().size(),
                    variant.getVariantStock(),
                    variant.getVariantPrice(),
                    variant.getRowStatus()
            );
            displayedIndexes.add(idx);
        }

        return displayedIndexes;
    }

    /*
        Esta función la cree para simular el inicio de la aplicación con una carga de datos que provendría desde la db
        Crea los objetos mínimos necesarios para una demostración de flujo del sistema y los almacena en una lista
     */
    private static void preChrageData() {
        /*
            Creación de usuarios
         */
        users.addAll(
                List.of(
                        new Users(
                                UserRole.ADMIN,
                                "admin",
                                "Administrador",
                                "admin@admin.com",
                                null,
                                null
                        ),
                        new Users(
                                UserRole.CUSTOMER,
                                "cust",
                                "Customer",
                                "cust@cust.com",
                                null,
                                null
                        )
                )
        );

        actualUser = users.getFirst();

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