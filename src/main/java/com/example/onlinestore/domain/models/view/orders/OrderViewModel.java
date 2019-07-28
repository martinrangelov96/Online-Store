package com.example.onlinestore.domain.models.view.orders;

import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
import com.example.onlinestore.domain.models.view.users.UserProfileViewModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderViewModel {

    private String id;
    private List<ProductDetailsViewModel> products;
    private UserProfileViewModel customer;
    private LocalDateTime orderedOn;
    private BigDecimal totalPrice;
    private List<ProductDetailsViewModel> productsUnique;

    public OrderViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProductDetailsViewModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDetailsViewModel> products) {
        this.products = products;
    }

    public UserProfileViewModel getCustomer() {
        return customer;
    }

    public void setCustomer(UserProfileViewModel customer) {
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

    public List<ProductDetailsViewModel> getProductsUnique() {
        return productsUnique;
    }

    public void setProductsUnique(List<ProductDetailsViewModel> productsUnique) {
        this.productsUnique = productsUnique;
    }
}
