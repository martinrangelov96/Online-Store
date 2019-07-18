package com.example.onlinestore.services;

import com.example.onlinestore.domain.models.service.ProductServiceModel;

import java.util.List;

public interface ProductService {

    ProductServiceModel addProduct(ProductServiceModel productServiceModel);

    List<ProductServiceModel> findAllProducts();

    ProductServiceModel findProductById(String id);

    ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel);

    void deleteProduct(String id);
}
