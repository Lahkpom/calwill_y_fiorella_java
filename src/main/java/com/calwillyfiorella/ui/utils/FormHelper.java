package com.calwillyfiorella.ui.utils;

import com.calwillyfiorella.Main;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.Sale;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.AuxiliarFunction;
import java.util.function.Supplier;

public final class FormHelper {
    private FormHelper() {}

    /**
     * Construye el texto del prompt adaptándose a si es creación o actualización.
     */
    public static String buildPrompt(String label, Object currentValue, boolean isUpdate) {
        System.out.println(Main.HYPHEN_SEPARATOR);
        if (!isUpdate || currentValue == null)
            return String.format("Ingrese %s: ", label);

        return String.format("""
                %s actual: %s.
                Si desea modificarlo ingrese el nuevo %s: """,
                label,
                currentValue,
                label
        );
    }

    /**
     * Andamio genérico para formularios de creación/modificación de objetos por consola.
     *
     * @param entityName       Nombre visible ("COLOR", "PRODUCTO", "VARIANTE")
     * @param isUpdate         Indica si es edición
     * @param currentData      null si es creación, instancia actual si es edición (debe extender BaseEntity. Pide RowStatus por defecto)
     * @param entityFormFiller Función lambda donde cada entidad pide sus campos específicos
     * @param <T>              Tipo de la entidad
     * @return La nueva instancia cargada o null si ocurrió un error.
     */
    public static <T extends BaseEntity> T executeCreateOrUpdateForm(
            String      entityName,
            boolean     isUpdate,
            T           currentData,
            Supplier<T> entityFormFiller
    ) {
        if (isUpdate && currentData == null)
            throw new IllegalArgumentException(
                    String.format(
                            "Se indicó que es un update pero no se entregó el/la %s actual.",
                            entityName.toLowerCase()
                    )
            );

        String  action      = isUpdate ? "actualizar" : "crear";
        boolean isStrict    = !isUpdate;

        MenuHelper.printMenuTitle(
                String.format(
                        "FORMULARIO PARA %s %s",
                        action,
                        entityName
                )
        );

        if (isUpdate) {
            System.out.println("¡¡¡ Para mantener el valor anterior en cada opción solo presione enter !!!");
            System.out.println(Main.HYPHEN_SEPARATOR);
        }

        try {
            // Ejecutar la lectura de los campos específicos de cada entidad
            T newEntityData = entityFormFiller.get();

            // Leer el RowStatus común a todas las clases (Se excluye cuando se trata de la actualizaciónd de datos de un usuario o de una venta).
            if (!(newEntityData instanceof Users || newEntityData instanceof Sale)) {
                RowStatus newRowStatus = RowStatus.ACTIVE;

                if (isUpdate) {
                    String prompt = buildPrompt(
                            "Estado",
                            currentData.getRowStatus(),
                            true
                    );
                    newRowStatus = AuxiliarFunction.requireRowStatus(prompt, isStrict);
                }

                newEntityData.setRowStatus(newRowStatus);
            }

            System.out.println(Main.HYPHEN_SEPARATOR);

            return newEntityData;

        } catch (Exception e) {
            System.err.format("Error al %s el/la %s: %s%n", action, entityName.toLowerCase(), e.getMessage());
            return null;
        }
    }
}