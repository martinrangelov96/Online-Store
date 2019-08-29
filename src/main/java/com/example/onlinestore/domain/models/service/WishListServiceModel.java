package com.example.onlinestore.domain.models.service;

import java.util.List;
import java.util.Set;

public class WishListServiceModel {

    private Set<ProductServiceModel> products;
    private UserServiceModel customer;

    public WishListServiceModel() {
    }

    public Set<ProductServiceModel> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductServiceModel> products) {
        this.products = products;
    }

    public UserServiceModel getCustomer() {
        return customer;
    }

    public void setCustomer(UserServiceModel customer) {
        this.customer = customer;
    }
}
