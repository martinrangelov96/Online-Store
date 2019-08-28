package com.example.onlinestore.domain.models.service;

import java.util.List;

public class WishListServiceModel {

    private List<ProductServiceModel> products;
    private UserServiceModel customer;

    public WishListServiceModel() {
    }

    public List<ProductServiceModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductServiceModel> products) {
        this.products = products;
    }

    public UserServiceModel getCustomer() {
        return customer;
    }

    public void setCustomer(UserServiceModel customer) {
        this.customer = customer;
    }
}
