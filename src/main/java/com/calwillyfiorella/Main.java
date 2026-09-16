package com.calwillyfiorella;

import com.calwillyfiorella.model.*;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static List<Color> colors = new ArrayList<>();
    static Color col1 = new Color(
            1,
            "white",
            "white as the snow",
            "#ffffff",
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static Color col2 = new Color(
            2,
            "black",
            "black as the night",
            "#000000",
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static List<Size> sizes = new ArrayList<>();
    static Size size1 = new Size(
            1,
            "m",
            "medium",
            1,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static Size size2 = new Size(
            2,
            "l",
            "large",
            2,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static List<Product> products = new ArrayList<>();
    static Product prod1 = new Product(
            UUID.randomUUID(),
            Category.CALZADO,
            "Artículo_1",
            "Esta es la descripción corta del Artículo_1",
            "Esta es la descripción larga del Artículo_1",
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static Product prod2 = new Product(
            UUID.randomUUID(),
            Category.CALZADO,
            "Artículo_2",
            "Esta es la descripción corta del Artículo_2",
            "Esta es la descripción larga del Artículo_2",
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    static List<ProductVariant> variants = new ArrayList<>();
    static ProductVariant var1 = new ProductVariant(
            UUID.randomUUID(),
            prod1,
            col1,
            size1,
            TargetGender.NINIOS,
            "Descripción de la variante var1 del Artículo_1",
            "SKU-ART-1-VAR-1",
            new BigDecimal("10000"),
            3,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );
    static ProductVariant var2 = new ProductVariant(
            UUID.randomUUID(),
            prod1,
            col2,
            size2,
            TargetGender.NINIAS,
            "Descripción de la variante var2 del Artículo_1",
            "SKU-ART-1-VAR-2",
            new BigDecimal("11000"),
            3,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );
    static ProductVariant var3 = new ProductVariant(
            UUID.randomUUID(),
            prod2,
            col1,
            size1,
            TargetGender.HOMBRES,
            "Descripción de la variante var3 del Artículo_2",
            "SKU-ART-2-VAR-3",
            new BigDecimal("12000"),
            3,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );
    static ProductVariant var4 = new ProductVariant(
            UUID.randomUUID(),
            prod2,
            col2,
            size2,
            TargetGender.MUJERES,
            "Descripción de la variante var4 del Artículo_2",
            "SKU-ART-2-VAR-4",
            new BigDecimal("13000"),
            3,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
    );

    List<VariantImage> images = new ArrayList<>();

    public static void main(String[] args) {

    }


}