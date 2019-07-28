package com.example.onlinestore.domain.models.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceModel extends BaseServiceModel {

    private List<ProductServiceModel> products;
    private UserServiceModel customer;
    private LocalDateTime orderedOn;
    private BigDecimal totalPrice;
    private List<ProductServiceModel> productsUnique;

    public OrderServiceModel() {
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

    public LocalDateTime getOrderedOn() {
        return orderedOn;
    }

    public void setOrderedOn(LocalDateTime orderedOn) {
        this.orderedOn = orderedOn;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<ProductServiceModel> getProductsUnique() {
        return productsUnique;
    }

    public void setProductsUnique(List<ProductServiceModel> productsUnique) {
        this.productsUnique = productsUnique;
    }
}
