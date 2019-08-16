package com.example.onlinestore.integration.services;

import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import com.example.onlinestore.services.product.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository mockProductRepository;

    @MockBean
    private CloudinaryService mockCloudinaryService;

    @Test
    public void addProduct_whenValidImage_productCreated() throws IOException {
        ProductServiceModel productServiceModel = this.productService.addProduct(new ProductServiceModel());
        this.mockCloudinaryService.uploadImage(any());
        String productImageUrl = "http://imageUrl";
        productServiceModel.setImageUrl(productImageUrl);

        verify(this.mockProductRepository)
                .save(any());
    }

    @Test(expected = Exception.class)
    public void addProduct_whenNull_throwException() throws IOException {
        this.productService.addProduct(null);

        verify(this.mockProductRepository)
                .save(any());
    }

}
