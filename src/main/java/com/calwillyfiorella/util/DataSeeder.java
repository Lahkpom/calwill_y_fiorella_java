package com.calwillyfiorella.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Sale;
import com.calwillyfiorella.model.SaleItem;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.PaymentMethod;
import com.calwillyfiorella.model.enums.PaymentStatus;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.SaleStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.repository.CartRepository;
import com.calwillyfiorella.repository.ColorRepository;
import com.calwillyfiorella.repository.ProductRepository;
import com.calwillyfiorella.repository.UserRepository;
import com.calwillyfiorella.service.SaleService;

public final class DataSeeder {

    private DataSeeder() {}

    /**
     * Se simula inyección de la DB a las List de cada Repository
     *
     * @param userRepository    userRepository
     * @param productRepository productRepository
     * @param colorRepository   colorRepository
     * @param cartRepository    cartRepository
     * @param saleService       saleService
     */
    public static void seed(
            UserRepository      userRepository,
            ProductRepository   productRepository,
            ColorRepository     colorRepository,
            CartRepository      cartRepository,
            SaleService         saleService
    ) {
        // Usuarios iniciales
        Users admin = new Users(
                UserRole.ADMIN,
                "admin",
                "Administrador",
                "admin@admin.com",
                null, null
        );
        Users customer = new Users(
                UserRole.CUSTOMER,
                "cust",
                "Customer",
                "cust@cust.com",
                null, null
        );
        userRepository.save(admin);
        userRepository.save(customer);

        // Colores iniciales
        Color white = new Color(
                "white",
                "white as the snow",
                "#ffffff"
        );
        Color black = new Color(
                "black",
                "black as the night",
                "#000000"
        );
        colorRepository.save(white);
        colorRepository.save(black);

        // Productos y variantes
        Product art1 = new Product(
                Category.CALZADO,
                "Artículo_1",
                "Desc corta 1",
                "Desc larga 1"
        );
        ProductVariant variant1 = new ProductVariant(
                        art1,
                        white,
                        NumericSize.T_18,
                        TargetGender.NINIOS,
                        "Var 1",
                        "SKU-1",
                        new BigDecimal("10000"),
                        50
        );
        ProductVariant variant2 = new ProductVariant(
                        art1,
                        black,
                        NumericSize.T_19,
                        TargetGender.NINIAS,
                        "Var 2",
                        "SKU-2",
                        new BigDecimal("11000"),
                        50
        );
        art1.saveVariant(variant1);
        art1.saveVariant(variant2);

        Product art2 = new Product(
                Category.CALZADO,
                "Artículo_2",
                "Desc corta 2",
                "Desc larga 2"
        );
        ProductVariant variant3 = new ProductVariant(
                        art2,
                        white,
                        NumericSize.T_18,
                        TargetGender.HOMBRES,
                        "Var 3",
                        "SKU-3",
                        new BigDecimal("12000"),
                        50
        );
        ProductVariant variant4 = new ProductVariant(
                        art2,
                        black,
                        NumericSize.T_19,
                        TargetGender.MUJERES,
                        "Var 4",
                        "SKU-4",
                        new BigDecimal("13000"),
                        50
        );
        art2.saveVariant(variant3);
        art2.saveVariant(variant4);

        productRepository.save(art1);
        productRepository.save(art2);
        // Se inyecta también un producto inactivo
        productRepository.save(
                new Product(
                        UUID.randomUUID(),
                        Category.CALZADO,
                        "Artículo_Inactivo",
                        "Desc corta",
                        "Desc larga",
                        RowStatus.INACTIVE,
                        LocalDateTime.now(),
                        null
                )
        );

                seedCustomerData(admin, List.of(variant1, variant2), saleService, cartRepository);
                seedCustomerData(customer, List.of(variant3, variant4), saleService, cartRepository);
        }

        private static void seedCustomerData(
                        Users user,
                        List<ProductVariant> cartVariants,
                        SaleService saleService,
                        CartRepository cartRepository
        ) {
                for (int i = 0; i < 3; i++) {
                        SaleStatus saleStatus = i == 2 ? SaleStatus.ENTREGADO : (i == 0 ? SaleStatus.PENDIENTE : SaleStatus.PREPARACION);
                        PaymentStatus paymentStatus = i == 2 ? PaymentStatus.APROBADO : PaymentStatus.PENDIENTE;
                        ProductVariant variant = cartVariants.get(i % cartVariants.size());
                        Sale sale = new Sale();
                        sale.setCustomerName(user.getName());
                        sale.setCustomerEmail(user.geteMail());
                        sale.setCustomerPhone(user.getPhone());
                        sale.setShippingAddress("Dirección de ejemplo " + (i + 1));
                        sale.setShippingCost(BigDecimal.ZERO);
                        sale.setSaleNotes("Venta de ejemplo " + (i + 1));
                        sale.setSaleStatus(saleStatus);
                        sale.setPaymentMethod(PaymentMethod.EFECTIVO);
                        sale.setPaymentStatus(paymentStatus);

                        SaleItem item = new SaleItem(
                                        null,
                                        null,
                                        variant,
                                        variant.getDesc(),
                                        variant.getProduct().getName(),
                                        variant.getSku(),
                                        variant.getPrice(),
                                        1,
                                        null,
                                        null
                        );
                        saleService.createSale(sale, user, List.of(item));
                }

                for (ProductVariant variant : cartVariants) {
                        cartRepository.save(new CartItem(user, variant, 1));
                }
    }
}