package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorRepository {
    private final AtomicInteger idSequence;

    private final List<Color> colors;

    public ColorRepository() {
        idSequence  = new AtomicInteger(0);
        colors      = new ArrayList<>();
    }

    public List<Color> findAll() { return Collections.unmodifiableList(colors); }

    public void save(Color color) {
        if (color.getId() == null) color.setId(idSequence.incrementAndGet());

        colors.add(color);
    }

    public Optional<Color> findByName(String colorName) {
        return this.colors.stream()
                .filter(c -> c.getName().equals(colorName))
                .findFirst();
    }

    public Optional<Color> findById(Integer colorId) {
        return this.colors.stream().filter(
                c -> c.getId().equals(colorId)
        ).findFirst();
    }

    public Optional<Color> findByHexCod(String colorCode) {
        return this.colors.stream().filter(
                c -> c.getCode().equals(colorCode)
        ).findFirst();
    }

    public void delete(Color color) { colors.remove(color); }

}
