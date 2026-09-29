package com.calwillyfiorella.ui.viewUtils;

import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.ui.menuUtils.MenuHelper;

import java.util.ArrayList;
import java.util.List;

public final class ListPrinter {

    private ListPrinter() {}

    /**
     * Lista cualquier entidad que herede de BaseEntity, filtra por estado activo dependiendo si quien llama a la función es administrador, devuelve lista con los índices disponibles de acuerdo al filtrado.
     *
     * @param title     Título a mostrar en consola.
     * @param items     Lista de elementos a iterar.
     * @param isAdmin   true si debe incluir registros no activos/eliminados.
     * @param <T>       Solo tipos que hereden de BaseEntity, ya que filtra por RowStatus.
     * @return          Lista de índices válidos mostrados al usuario.
     */
    public static <T extends BaseEntity> List<Integer> renderList(
            String title,
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

            if (!isAdmin && item.getRowStatus() != RowStatus.ACTIVE) continue;

            System.out.format("Opción %d. %s%n", idx, item);
            displayedIndexes.add(idx);
        }

        return displayedIndexes;
    }
}