package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.ColorService;
import com.calwillyfiorella.service.ProductService;
import com.calwillyfiorella.ui.viewUtils.ListPrinter;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.ui.menuUtils.*;
import com.calwillyfiorella.util.InputUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AdminMenu {
    private final ProductService    productService;
    private final ColorService      colorService;
    private final AuthMenu          authMenu;
    private final MenuHelper        menuHelper;
    private final Runnable          mainMenu;

    public AdminMenu(
            ProductService  productService,
            ColorService    colorService,
            AuthMenu        authMenu,
            MenuHelper      menuHelper,
            Runnable        mainMenu
    ) {
        this.productService = Objects.requireNonNull(productService);
        this.colorService   = Objects.requireNonNull(colorService);
        this.authMenu       = Objects.requireNonNull(authMenu);
        this.menuHelper     = Objects.requireNonNull(menuHelper);
        this.mainMenu       = Objects.requireNonNull(mainMenu);
    }

    public void render() {
        while (!AuthService.actualUserIsAdmin()) {
            authMenu.render(true, mainMenu);
        }

        List<MenuOption> options = List.of(
                MenuOption.of("Gestionar Productos"         , this::renderAdminProducts),
                MenuOption.of("Gestionar Colores"           , this::renderAdminColors),
                MenuOption.of("Gestionar Ventas"            , () -> System.out.println("FUNCIÓN EN DESARROLLO")),
                MenuOption.of("Crear Usuario Administrador" , () -> authMenu.signUp(true))
        );

        menuHelper.renderMenuOptions(options, mainMenu);
    }

    private void renderAdminColors() {
        List<Color> colors = colorService.getAll();
        List<Integer> allowedOptions = AuxiliarFunction.toListColors(colors, true);

        if (allowedOptions.isEmpty()) render();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar un Color"     , () -> updateColor(colors, allowedOptions)),
                MenuOption.of("Crear un nuevo Color", this::createColor)
        );

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateColor(List<Color> colors, List<Integer> allowedOptions) {
        Integer colorIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del color: ", false);
        Color currentColorData = colors.get(colorIdx - 1);

        try {
            Color newColorData = createOrUpdateColorForm(true, currentColorData);

            if (newColorData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de actualización de productos es un objeto nulo.");

            if (newColorData.getName() == null) newColorData.setName(currentColorData.getName());
            if (newColorData.getDesc() == null) newColorData.setDesc(currentColorData.getDesc());
            if (newColorData.getCode() == null) newColorData.setCode(currentColorData.getCode());
            if (newColorData.getRowStatus() == null) newColorData.setRowStatus(currentColorData.getRowStatus());

            colorService.updateColor(currentColorData.getId(), newColorData);

            System.out.println("El color fue actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private void createColor() {
        try {
            Color newColorData = createOrUpdateColorForm(false, null);

            if (newColorData == null)
                throw new IllegalStateException("El Color devuelto por el formulario de creación de Colores es un objeto nulo.");

            colorService.createColor(newColorData);

            System.out.println("el Color fue creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear el Color: " + e.getMessage());
        }

        renderAdminColors();
    }
    private Color createOrUpdateColorForm(boolean isUpdate, Color currentColorData) {
        if (isUpdate && currentColorData == null)
            throw new IllegalArgumentException("Se indicó que es un update pero no se entregó el color actual.");

        boolean isStrict    = !isUpdate;
        String  action      = (isUpdate) ? "actualizar" : "crear";

        MenuHelper.printMenuTitle(
                String.format(
                        "FORMULARIO PARA %s COLOR: ",
                        action
                )
        );

        if (isUpdate)
            System.out.format("""
                    ¡¡¡ Para mantener el valor anterior en cada opción solo presione enter !!!
                    -----------------------------------------------------------
                    """);

        try {
            // NAME
            String newName = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Nombre actual: %s.
                                    Si desea modificarlo ingrese el nuevo Nombre:
                                    """,
                            currentColorData.getName()
                    )
                            : "Ingrese el Nombre:",
                    isStrict
            );

            // DESC
            String newDesc = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Descripción actual: %s.
                                    Si desea modificarlo ingrese la nueva Descripción:
                                    """,
                            currentColorData.getName()
                    )
                            : "Ingrese la Descripción:",
                    isStrict
            );

            // LONG DESC
            String newCode = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Descripción Larga actual: %s.
                                    Si desea modificarlo ingrese el nuevo código:
                                    """,
                                    currentColorData.getCode()
                    )
                            : "Ingrese el código:",
                    isStrict
            );

            // ROW STATUS
            RowStatus newRowStatus = null;
            if (isUpdate)
                newRowStatus = AuxiliarFunction.requireRowStatus(
                        String.format("""
                                    Estado actual: %s.
                                    Si desea modificarlo ingrese el número del nuevo Estado:
                                    """,
                                currentColorData.getRowStatus()
                        ),
                        isStrict
                );

            Color newColorData = new Color();
            newColorData.setName(newName);
            newColorData.setDesc(newDesc);
            newColorData.setCode(newCode);
            newColorData.setRowStatus(newRowStatus);

            return newColorData;
        } catch (Exception e) {
            System.err.format(
                    "Error al %s el color: %s",
                    action,
                    e.getMessage()
            );
            return null;
        }
    }

    private void renderAdminProducts() {
        List<Product> products = productService.getAll();
        List<Integer> allowedOptions = ListPrinter.renderList("LISTADO DE PRODUCTOS", products, true);

        if (allowedOptions.isEmpty()) render();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar un Producto"                  , () -> updateProduct(products, allowedOptions)),
                MenuOption.of("Gestionar Variantes de un Producto"  , () -> renderAdminVariants(products, allowedOptions)),
                MenuOption.of("Crear un nuevo Producto"             , this::createProduct)
        );

        menuHelper.renderMenuOptions(options, this::render);
    }
    private void updateProduct(List<Product> products, List<Integer> allowedOptions) {
        Integer productIdx = AuxiliarFunction.requireUserOption(allowedOptions, "Número del producto: ", false);
        Product currentProductData = products.get(productIdx - 1);

        try {
            Product newProductData = createOrUpdateProductForm(true, currentProductData);

            if (newProductData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de actualización de productos es un objeto nulo.");

            newProductData.setCategory(Category.CALZADO);
            if (newProductData.getName() == null) newProductData.setName(currentProductData.getName());
            if (newProductData.getShortDesc() == null) newProductData.setShortDesc(currentProductData.getShortDesc());
            if (newProductData.getLongDesc() == null) newProductData.setLongDesc(currentProductData.getLongDesc());
            if (newProductData.getRowStatus() == null) newProductData.setRowStatus(currentProductData.getRowStatus());

            productService.updateProduct(currentProductData.getId(), newProductData);

            System.out.println("El producto fue actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private void createProduct() {
        try {
            Product newProductData = createOrUpdateProductForm(false, null);

            if (newProductData == null)
                throw new IllegalStateException("El producto devuelto por el formulario de creación de productos es un objeto nulo.");

            productService.createProduct(newProductData);

            System.out.println("el Producto fue creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private Product createOrUpdateProductForm(boolean isUpdate, Product currentProductData) {
        if (isUpdate && currentProductData == null)
            throw new IllegalArgumentException("Se indicó que es un update pero no se entregó el producto actual.");

        boolean isStrict    = !isUpdate;
        String  action      = (isUpdate) ? "actualizar" : "crear";

        MenuHelper.printMenuTitle(
                String.format(
                        "FORMULARIO PARA %s PRODUCTO: ",
                        action
                )
        );

        if (isUpdate)
            System.out.format("""
                    ¡¡¡ Para mantener el valor anterior en cada opción solo presione enter !!!
                    -----------------------------------------------------------
                    """);

        try {
            // CATEGORY (POR EL MOMENTO SOLO TENEMOS CATEGORÍA CALZADO)
            Category newCategory = Category.CALZADO;

            // NAME
            String newName = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Nombre actual: %s.
                                    Si desea modificarlo ingrese el nuevo Nombre:
                                    """,
                                    currentProductData.getName()
                    )
                            : "Ingrese el Nombre:",
                    isStrict
            );

            // SHORT DESC
            String newShortDesc = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Descripción Corta actual: %s.
                                    Si desea modificarlo ingrese la nueva Descripción Corta:
                                    """,
                                    currentProductData.getShortDesc()
                    )
                            : "Ingrese la Descripción Corta:",
                    isStrict
            );

            // LONG DESC
            String newLongDesc = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Descripción Larga actual: %s.
                                    Si desea modificarlo ingrese la nueva Descripción Larga:
                                    """,
                                    currentProductData.getLongDesc()
                    )
                            : "Ingrese la Descripción Larga:",
                    isStrict
            );

            // ROW STATUS
            RowStatus newRowStatus = null;
            if (isUpdate)
                newRowStatus = AuxiliarFunction.requireRowStatus(
                        String.format("""
                                    Estado actual: %s.
                                    Si desea modificarlo ingrese el número del nuevo Estado:
                                    """,
                                    currentProductData.getRowStatus()
                        ),
                        isStrict
                );

            Product newProductData = new Product();
            newProductData.setCategory(newCategory);
            newProductData.setName(newName);
            newProductData.setShortDesc(newShortDesc);
            newProductData.setLongDesc(newLongDesc);
            newProductData.setRowStatus(newRowStatus);

            return newProductData;
        } catch (Exception e) {
            System.err.format(
                    "Error al %s el producto: %s",
                    action,
                    e.getMessage()
            );
            return null;
        }
    }

    private void renderAdminVariants(List<Product> products, List<Integer> allowedProductOptions) {
        Integer idx = AuxiliarFunction.requireUserOption(allowedProductOptions, "Número del producto: ", true);
        Product product = products.get(idx - 1);

        List<ProductVariant> variants = product.getVariants();

        List<Integer> allowedVariantOptions = ListPrinter.renderList("VARIANTES DE " + product.getName(), variants, true);

        if (allowedVariantOptions.isEmpty()) renderAdminProducts();

        List<MenuOption> options = List.of(
                MenuOption.of("Editar una Variante"                     , () -> updateVariant(product, variants, allowedVariantOptions)),
                MenuOption.of("Gestionar las Imágenes de una Variante"  , null),
                MenuOption.of("Crear una nueva Variante"                , () -> createVariant(product))
        );

        menuHelper.renderMenuOptions(options, this::renderAdminProducts);
    }
    private void updateVariant(Product product, List<ProductVariant> variants, List<Integer> allowedVariantOptions) {
        Integer variantIdx = AuxiliarFunction.requireUserOption(allowedVariantOptions, "Número de la variante: ", true);
        ProductVariant currentVariantData = variants.get(variantIdx - 1);

        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(true, currentVariantData);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de actualización de variantes es un objeto nulo.");

            if (newVariantData.getColor() == null) newVariantData.setColor(currentVariantData.getColor());
            if (newVariantData.getSize() == null) newVariantData.setSize(currentVariantData.getSize());
            if (newVariantData.getTargetGender() == null) newVariantData.setTargetGender(currentVariantData.getTargetGender());
            if (newVariantData.getVariantDesc() == null) newVariantData.setVariantDesc(currentVariantData.getVariantDesc());
            if (newVariantData.getVariantSku() == null) newVariantData.setVariantSku(currentVariantData.getVariantSku());
            if (newVariantData.getVariantPrice() == null) newVariantData.setVariantPrice(currentVariantData.getVariantPrice());
            if (newVariantData.getVariantStock() == null) newVariantData.setVariantStock(currentVariantData.getVariantStock());
            if (newVariantData.getRowStatus() == null) newVariantData.setRowStatus(currentVariantData.getRowStatus());

            product.updateVariant(currentVariantData.getVariantId(), newVariantData);

            System.out.println("La variante fue actualizada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la variante: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private void createVariant(Product product) {
        try {
            ProductVariant newVariantData = createOrUpdateVariantForm(false, null);

            if (newVariantData == null)
                throw new IllegalStateException("La variante devuelta por el formulario de creación de variantes es un objeto nulo.");

            product.addVariant(newVariantData);

            System.out.println("La variante fue creada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear la variante: " + e.getMessage());
        }

        renderAdminProducts();
    }
    private ProductVariant createOrUpdateVariantForm(boolean isUpdate, ProductVariant currentVariantData) {
        if (isUpdate && currentVariantData == null)
            throw new IllegalArgumentException("Se indicó que es un update pero no se entregó la variante actual.");

        boolean isStrict    = !isUpdate;
        String  action      = (isUpdate) ? "actualizar" : "crear";

        MenuHelper.printMenuTitle(
                String.format(
                        "FORMULARIO PARA %s VARIANTE: ",
                        action
                )
        );

        if (isUpdate)
            System.out.format("""
                    ¡¡¡ Para mantener el valor anterior en cada opción solo presione enter !!!
                    -----------------------------------------------------------
                    """);

        try {
            // COLOR
            Color newColor = AuxiliarFunction.requireColor(
                    colorService.getAll(),
                    isUpdate
                            ? String.format("""
                                    Color actual: %s.
                                    Si desea modificarlo ingrese el número del nuevo color:
                                    """,
                                    currentVariantData.getColor()
                            )
                            : "Ingrese el número del color:",
                    true,
                    isStrict
            );

            // SIZE
            NumericSize newSize = AuxiliarFunction.requireNumericSize(
                    isUpdate
                            ? String.format("""
                                    Talle actual: %s.
                                    Si desea modificarlo ingrese el número de la opción de nuevo Talle:
                                    """,
                                    currentVariantData.getSize()
                            )
                            : "Ingrese el número de la opción del Talle:",
                    isStrict
            );

            // TARGET GENDER
            TargetGender newTargetGender = AuxiliarFunction.requireTargetGender(
                    isUpdate
                            ? String.format("""
                                    Género actual: %s.
                                    Si desea modificarlo ingrese el número del nuevo Género:
                                    """,
                                    currentVariantData.getTargetGender()
                            )
                            : "Ingrese el número del Género:",
                    isStrict
            );

            // DESCRIPTION
            String newDesc = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    Descripción actual: %s.
                                    Si desea modificarlo ingrese la nueva descripción:
                                    """,
                                    currentVariantData.getVariantDesc()
                            )
                            : "Ingrese la Descripción:",
                    isStrict
            );

            // SKU
            String newSKU = InputUtils.readString(
                    isUpdate
                            ? String.format("""
                                    SKU actual: %s.
                                    Si desea modificarlo ingrese el nuevo SKU:
                                    """,
                                    currentVariantData.getVariantSku()
                            )
                            : "Ingrese el SKU:",
                    isStrict
            );

            // PRICE
            BigDecimal newPrice = InputUtils.readPrice(
                    isUpdate
                            ? String.format("""
                                    Precio actual: %s.
                                    Si desea modificarlo ingrese el nuevo Precio:
                                    """,
                                    currentVariantData.getVariantPrice()
                            )
                            : "Ingrese el Precio:",
                    isStrict
            );

            // STOCK
            Integer newStock = InputUtils.readInt(
                    isUpdate
                            ? String.format("""
                                    Stock actual: %d.
                                    Si desea modificarlo ingrese la nueva cantidad de stock:
                                    """,
                                    currentVariantData.getVariantStock()
                            )
                            : "Ingrese el Stock:",
                    isStrict
            );

            // ROW STATUS
            RowStatus newRowStatus = null;
            if (isUpdate)
                newRowStatus = AuxiliarFunction.requireRowStatus(
                        String.format("""
                                    Estado actual: %s.
                                    Si desea modificarlo ingrese el número del nuevo Estado:
                                    """,
                                    currentVariantData.getRowStatus()
                        ),
                        isStrict
                );

            ProductVariant newVariantData = new ProductVariant();
            newVariantData.setColor(newColor);
            newVariantData.setSize(newSize);
            newVariantData.setTargetGender(newTargetGender);
            newVariantData.setVariantDesc(newDesc);
            newVariantData.setVariantSku(newSKU);
            newVariantData.setVariantPrice(newPrice);
            newVariantData.setVariantStock(newStock);
            newVariantData.setRowStatus(newRowStatus);

            return newVariantData;
        } catch (Exception e) {
            System.err.format(
                    "Error al %s la variante: %s",
                    action,
                    e.getMessage()
            );
            return null;
        }
    }











}