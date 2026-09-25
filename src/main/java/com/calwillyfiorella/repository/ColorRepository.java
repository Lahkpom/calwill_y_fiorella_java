package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ColorRepository {
    private final List<Color> colors;

    public ColorRepository() { colors = new ArrayList<>(); }

    public List<Color> findAll() { return Collections.unmodifiableList(colors); }

    public void save(Color color) { colors.add(color); }

    public Optional<Color> findByName(String colorName) {
        return this.colors.stream().filter(
                c -> c.getColorName().equals(colorName)
        ).findFirst();
    }

    public Optional<Color> findByHexCod(String colorCode) {
        return this.colors.stream().filter(
                c -> c.getColorCode().equals(colorCode)
        ).findFirst();
    }

    // TODO Hacer que cambie el estado a inactive o deleted sin sacarlo de la lista
    public void delete(Color color) { colors.remove(color); }
}
