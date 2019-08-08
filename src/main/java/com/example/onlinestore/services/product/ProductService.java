package com.example.onlinestore.services.product;

import com.example.onlinestore.domain.models.service.ProductServiceModel;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductServiceModel addProduct(ProductServiceModel productServiceModel) throws IOException;

    List<ProductServiceModel> findAllProducts();

    ProductServiceModel findProductById(String id);

    ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel) throws IOException;

    void deleteProduct(String id);

    List<ProductServiceModel> findAllByCategory(String category);

    ProductServiceModel updateQuantityAfterAddingToCart(String id, int quantity);

    ProductServiceModel updateQuantityAfterRemovingFromCart(String id, int quantity);
}
