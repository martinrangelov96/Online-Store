package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Category;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.errors.ProductNotFoundException;
import com.example.onlinestore.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductServiceModel addProduct(ProductServiceModel productServiceModel) {
        Product product = this.modelMapper.map(productServiceModel, Product.class);

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
                .orElseThrow(() -> new ProductNotFoundException("Product with this id does not exist!"));

        this.productRepository.save(product);

        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with this id does not exist!"));

        productServiceModel.setCategories(
                this.categoryService.findAllCategories()
                .stream()
                .filter(categoryServiceModel -> productServiceModel.getCategories().contains(categoryServiceModel.getId()))
                .collect(Collectors.toList())
        );

        product.setName(productServiceModel.getName());
        product.setDescription(productServiceModel.getDescription());
        product.setPrice(productServiceModel.getPrice());
        product.setCategories(
                productServiceModel.getCategories()
                .stream()
                .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, Category.class))
                .collect(Collectors.toList())
        );

        this.productRepository.save(product);

        return this.modelMapper.map(product, ProductServiceModel.class);

        //TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.example.onlinestore.domain.entities.Category
        //save the transient instance before flushing: com.example.onlinestore.domain.entities.Category
//        product.setCategories(
//                productServiceModel.getCategories()
//                        .stream()
//                        .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, Category.class))
//                        .collect(Collectors.toList()));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with this id does not exist!"));

        this.productRepository.delete(product);
    }

    @Override
    public List<ProductServiceModel> findAllByCategory(String category) {
        //TODO: OPTIMIZE FILTERING

        List<ProductServiceModel> products = this.productRepository.findAll()
                .stream()
                .filter(product -> product.getCategories().stream().anyMatch(categoryStream -> categoryStream.getName().equals(category)))
                .map(product -> this.modelMapper.map(product, ProductServiceModel.class))
                .collect(Collectors.toList());

        return products;
    }
}
