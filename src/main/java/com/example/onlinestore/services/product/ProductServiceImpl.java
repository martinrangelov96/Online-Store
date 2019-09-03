package com.example.onlinestore.services.product;

import com.example.onlinestore.domain.entities.Category;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.errors.ProductNotFoundException;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.EMPTY_STRING;
import static com.example.onlinestore.constants.Constants.PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class ProductServiceImpl implements ProductService {

    private final static int NUMBER_OF_MINUTES = 30;
    //loading order every 30 minutes (in running app)
    private final static int TIME_TO_LOAD_ORDERS = 1000 * 60 * NUMBER_OF_MINUTES;

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ProductServiceModel addProduct(ProductServiceModel productServiceModel) throws IOException {
        Product product = this.modelMapper.map(productServiceModel, Product.class);

        String imageUrl = this.cloudinaryService.uploadImage(productServiceModel.getImage());
        product.setImageUrl(imageUrl);

        this.productRepository.save(product);
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public List<ProductServiceModel> findAllProducts() {
        List<ProductServiceModel> productServiceModels = this.productRepository.findAll()
                .stream()
                .map(product -> this.modelMapper.map(product, ProductServiceModel.class))
                .collect(Collectors.toList());

        return productServiceModels;
    }

    @Override
    public ProductServiceModel findProductById(String id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        this.productRepository.save(product);
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel) throws IOException {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        product.setName(productServiceModel.getName());
        product.setDescription(productServiceModel.getDescription());
        product.setPrice(productServiceModel.getPrice());
        product.setCategories(
                productServiceModel.getCategories()
                        .stream()
                        .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, Category.class))
                        .collect(Collectors.toList())
        );
        if (!EMPTY_STRING.equals(productServiceModel.getImage().getOriginalFilename())) {
            String imageUrl = this.cloudinaryService.uploadImage(productServiceModel.getImage());
            product.setImageUrl(imageUrl);
        }

        this.productRepository.save(product);
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        this.productRepository.delete(product);
    }

    @Override
    public List<ProductServiceModel> findAllByCategory(String category) {
        //Custom query in ProductRepository, faster than stream + filter
        List<ProductServiceModel> productsContainingCategory = this.productRepository.findAllProductsByCategory(category)
                .stream()
                .map(product -> this.modelMapper.map(product, ProductServiceModel.class))
                .collect(Collectors.toList());

        return productsContainingCategory;
    }

    @Override
    public ProductServiceModel updateQuantityAfterAddingToCart(String id, int quantity) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);

        this.productRepository.saveAndFlush(product);
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public ProductServiceModel updateQuantityAfterRemovingFromCart(String id, int quantity) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        product.setQuantityAvailable(product.getQuantityAvailable() + quantity);

        this.productRepository.save(product);
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Scheduled(fixedRate = TIME_TO_LOAD_ORDERS)
    private void deliverProducts() {
        List<Product> products = this.productRepository.findAll();
        Random random = new Random();

        for (Product product : products) {
            int quantity = random.nextInt(11);

            product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
            this.productRepository.save(product);
        }
        System.out.println("Products delivery! " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")));
    }
}
