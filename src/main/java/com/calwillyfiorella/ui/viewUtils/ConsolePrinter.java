package com.calwillyfiorella.ui.viewUtils;

import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.ui.menuUtils.MenuHelper;

import java.util.ArrayList;
import java.util.List;

public final class ConsolePrinter {

    private ConsolePrinter() {}

    /**
     * Lista cualquier entidad que herede de BaseEntity, numerando sus opciones.
     *
     * @param title      Título a mostrar en consola.
     * @param itemLabel  Prefijo ("Producto", "Color", "Talle", "Variante").
     * @param items      Lista de elementos a iterar.
     * @param isAdmin    true si debe incluir registros no activos.
     * @param <T>        Tipo que hereda de BaseEntity.
     * @return Lista de índices válidos (1-based) mostrados al usuario.
     */
    public static <T extends BaseEntity> List<Integer> renderEntityList(
            String title,
            String itemLabel,
            List<T> items,
            boolean isAdmin
    ) {
        List<Integer> displayedIndexes = new ArrayList<>();
        MenuHelper.printMenuTitle(title);

        if (items.isEmpty()) {
            System.out.println("No hay elementos para mostrar.");
            return displayedIndexes;
        }

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            int idx = i + 1;

            if (!isAdmin && item.getRowStatus() != RowStatus.ACTIVE) {
                continue;
            }

            System.out.format("%s %d. %s%n", itemLabel, idx, item);
            displayedIndexes.add(idx);
        }

        return displayedIndexes;
    }
}