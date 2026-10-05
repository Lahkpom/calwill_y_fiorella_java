package com.calwillyfiorella;

import com.calwillyfiorella.repository.CartRepository;
import com.calwillyfiorella.repository.ColorRepository;
import com.calwillyfiorella.repository.ProductRepository;
import com.calwillyfiorella.repository.SaleRepository;
import com.calwillyfiorella.repository.UserRepository;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.CartService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.service.ProductVariantService;
import com.calwillyfiorella.service.SaleService;
import com.calwillyfiorella.service.UserService;
import com.calwillyfiorella.ui.MainMenu;
import com.calwillyfiorella.util.DataSeeder;

public class Main {
    public static String HYPHEN_SEPARATOR = "------------------------------------------------------------------------";

    // * TODO - Desarrollar toda la parte de las ventas con las clases Sale y SaleItems
    // * TODO - Separar las responsabilidades entre Product y ProductVariant
    // * TODO - Unificar las responsabilidades entre ProductVariant y VariantImage como está hecho entre Sale y SaleItem
    // * TODO - Arreglar el flujo del carrito de compras reviviendo la clase Cart como objeto de negocio. La idea es que CartRepository almacene la List<CartIems> y que cuando el service me pida los item de un usuario, devolverselos de forma ordenada a través de un new Cart con userId, List<CartItems>, total. CartItem solo mantiene user, variant y quantity (eliminar el cartItemId porque no exste realmente)
    // * TODO - Generar otro menú en el admin para gestionar los usuarios existentes (Solo deberían poder verlo los SUPER_ADMIN)
    // * TODO - Hacer el variantImage service para mover toda la lógica de la clase ProductVariant
    // * TODO - Hacer que cuando inactivo un producto también se inactiven sus variantes, y que si modifico las variantes de un producto este se reactive


    public static void main(String[] args) {
        UserRepository      userRepository      = new UserRepository();
        ProductRepository   productRepository   = new ProductRepository();
        ColorRepository     colorRepository     = new ColorRepository();
        CartRepository      cartRepository      = new CartRepository();
        SaleRepository      saleRepository      = new SaleRepository();

        UserService             userService             = new UserService(userRepository);
        AuthService             authService             = new AuthService(userService);
        ProductService          productService          = new ProductService(productRepository);
        ProductVariantService   productVariantService   = new ProductVariantService(productService);
        ColorService            colorService            = new ColorService(colorRepository);
        CartService             cartService             = new CartService(cartRepository);
        SaleService             saleService             = new SaleService(saleRepository);

        DataSeeder.seed(
                userRepository,
                productRepository,
            colorRepository,
            cartRepository,
            saleService
        );

        MainMenu mainMenu = new MainMenu(
                authService,
                userService,
                productService,
                productVariantService,
                colorService,
                cartService,
                saleService
        );

        mainMenu.render();
    }
}