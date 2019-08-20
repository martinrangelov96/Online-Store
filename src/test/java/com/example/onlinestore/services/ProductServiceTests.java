package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Category;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import com.example.onlinestore.services.product.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTests {

    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(3.14);
    private static final String PRODUCT_NAME = "Test product";
    private static final String PRODUCT_IMAGE_URL = "http://imageUrl";
    private static final String PRODUCT_DESCRIPTION = "Product description";
    private static final Integer PRODUCT_QUANTITY_AVAILABLE = 10;


    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository mockProductRepository;

    @MockBean
    private CloudinaryService mockCloudinaryService;

    private List<Product> products;

    @Before
    public void setupTest() {
        this.products = new ArrayList<>();
        when(this.mockProductRepository.findAll())
                .thenReturn(this.products);
    }

    @Test
    public void addProduct_whenValidImage_productCreated() throws IOException {
        ProductServiceModel productServiceModel = this.productService.addProduct(new ProductServiceModel());
        this.mockCloudinaryService.uploadImage(any());
        productServiceModel.setImageUrl(PRODUCT_IMAGE_URL);

        verify(this.mockProductRepository)
                .save(any());
    }

    @Test(expected = Exception.class)
    public void addProduct_whenNull_throwException() throws IOException {
        this.productService.addProduct(null);

        verify(this.mockProductRepository)
                .save(any());
    }

    @Test
    public void findAllProducts_when1Product_return1Product() {
        Product product = new Product();
        product.setPrice(PRODUCT_PRICE);
        product.setName(PRODUCT_NAME);
        product.setImageUrl(PRODUCT_IMAGE_URL);
        product.setCategories(new ArrayList<>() {{
            new Category();
        }});
        product.setQuantityAvailable(PRODUCT_QUANTITY_AVAILABLE);
        product.setDescription(PRODUCT_DESCRIPTION);

        this.products.add(product);

        List<ProductServiceModel> actualProducts = this.productService.findAllProducts();
        ProductServiceModel actualProduct = actualProducts.get(0);

        assertEquals(product.getPrice(), actualProduct.getPrice());
        assertEquals(product.getName(), actualProduct.getName());
        assertEquals(product.getImageUrl(), actualProduct.getImageUrl());
        assertEquals(product.getQuantityAvailable(), actualProduct.getQuantityAvailable());
        assertEquals(product.getDescription(), actualProduct.getDescription());
        assertEquals(this.products.size(), actualProducts.size());
    }

    @Test
    public void findAllProducts_when0Products_returnEmptyList() {
        this.products.clear();
        List<ProductServiceModel> actualProducts = this.productService.findAllProducts();
        assertEquals(0, actualProducts.size());
    }

    @Test
    public void findProductById_whenValidProduct_returnProduct() {
        Product product = new Product();

        when(this.mockProductRepository.findById(any()))
                .thenReturn(java.util.Optional.of(product));
    }

    @Test
    public void findProductById_whenProductIsNull_throwException() {
        when(this.mockProductRepository.findById(null))
                .thenThrow(NullPointerException.class);
    }

//    @Test
//    public void findAllProductsByCategory_when2Products_return2Products() {
//        Product product1 = new Product();
//        product1.setPrice(PRODUCT_PRICE);
//        product1.setName(PRODUCT_NAME + "1");
//        product1.setImageUrl(PRODUCT_IMAGE_URL + "1");
//        product1.setQuantityAvailable(PRODUCT_QUANTITY_AVAILABLE + 1);
//        product1.setDescription(PRODUCT_DESCRIPTION + "1");
//        product1.setCategories(new ArrayList<>() {{
//            add(new Category() {{
//                setName("Sport");
//            }});
//            add(new Category() {{
//                setName("Art");
//            }});
//        }});
//
//        Product product2 = new Product();
//        product2.setPrice(BigDecimal.valueOf(5));
//        product2.setName(PRODUCT_NAME + "2");
//        product2.setImageUrl(PRODUCT_IMAGE_URL + "2");
//        product2.setQuantityAvailable(PRODUCT_QUANTITY_AVAILABLE + 2);
//        product2.setDescription(PRODUCT_DESCRIPTION + "2");
//        product2.setCategories(new ArrayList<>() {{
//            add(new Category() {{
//                setName("Clothes");
//            }});
//            add(new Category() {{
//                setName("Fitness");
//            }});
//        }});
//
//        this.products.add(product1);
//        this.products.add(product2);
//
//        List<Product> actualProductsByCategory = this.mockProductRepository.findAllProductsByCategory("Fitness");
//        Product actualProductByCategory = actualProductsByCategory.get(0);
//
//        assertEquals(product2.getPrice(), actualProductByCategory.getPrice());
//        assertEquals(product2.getName(), actualProductByCategory.getName());
//        assertEquals(product2.getImageUrl(), actualProductByCategory.getImageUrl());
//        assertEquals(product2.getDescription(), actualProductByCategory.getDescription());
//        assertEquals(product2.getCategories().size(), actualProductByCategory.getCategories().size());
//        assertEquals(product2.getQuantityAvailable(), actualProductByCategory.getQuantityAvailable());
//        assertEquals(1, actualProductsByCategory.size());
//    }

}
