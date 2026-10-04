package com.calwillyfiorella;

import com.calwillyfiorella.repository.*;
import com.calwillyfiorella.service.*;
import com.calwillyfiorella.ui.MainMenu;
import com.calwillyfiorella.util.DataSeeder;

public class Main {
    public static String HYPHEN_SEPARATOR = "------------------------------------------------------------------------";

    /**
     * TODO - Ver que cuando quiero gestionar las variantes de un producto no me rechace si no tiene ninguna, solo evitar que vea la opción de gestionar variantes (Tiene que poder crearlas)
     * TODO - Hacer que cuando inactivo un producto también se inactiven sus variantes, y que si modifico las variantes de un producto este se reactive
     * TODO - Desarrollar toda la parte de las ventas con las clases Sales y SaleItems
     * TODO - Revisar el CustomerMenu y hacer que render no reciva un callback para poder manejar mejor las demás funciones
     */

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