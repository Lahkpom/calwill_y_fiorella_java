package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Product;

import java.util.*;

public class ProductRepository {
    private final List<Product> products;

    public ProductRepository() { products = new ArrayList<>(); }

    public void save(Product product) { products.add(product); }

    public List<Product> findAll() { return Collections.unmodifiableList(products); }

    public Optional<Product> findByName(String name) {
        return this.products.stream().filter(p -> p.getProductName().equals(name)).findFirst();
    }

    public void delete(Product product) { products.remove(product); }
}
