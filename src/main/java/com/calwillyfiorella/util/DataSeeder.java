package com.calwillyfiorella.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.repository.ColorRepository;
import com.calwillyfiorella.repository.ProductRepository;
import com.calwillyfiorella.repository.UserRepository;

public final class DataSeeder {

    private DataSeeder() {}

    /**
     * Se simula inyección de la DB a las List de cada Repository
     *
     * @param userRepository    userRepository
     * @param productRepository productRepository
     * @param colorRepository   colorRepository
     */
    public static void seed(
            UserRepository      userRepository,
            ProductRepository   productRepository,
            ColorRepository     colorRepository
    ) {
        // Usuarios iniciales
        userRepository.save(
                new Users(
                        UserRole.ADMIN,
                        "admin",
                        "Administrador",
                        "admin@admin.com",
                        null, null
                )
        );
        userRepository.save(
                new Users(
                        UserRole.CUSTOMER,
                        "cust",
                        "Customer",
                        "cust@cust.com",
                        null, null
                )
        );

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
        art1.addVariant(
                white,
                NumericSize.T_18,
                TargetGender.NINIOS,
                "Var 1",
                "SKU-1",
                new BigDecimal("10000"),
                5
        );
        art1.addVariant(
                black,
                NumericSize.T_19,
                TargetGender.NINIAS,
                "Var 2",
                "SKU-2",
                new BigDecimal("11000"),
                5
        );

        Product art2 = new Product(
                Category.CALZADO,
                "Artículo_2",
                "Desc corta 2",
                "Desc larga 2"
        );
        art2.addVariant(
                white,
                NumericSize.T_18,
                TargetGender.HOMBRES,
                "Var 3",
                "SKU-3",
                new BigDecimal("12000"),
                5
        );
        art2.addVariant(
                black,
                NumericSize.T_19,
                TargetGender.MUJERES,
                "Var 4",
                "SKU-4",
                new BigDecimal("13000"),
                5
        );

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
    }
}