package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.binding.ProductAddBindingModel;
import com.example.onlinestore.domain.models.binding.ProductEditBindingModel;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.view.products.ProductDeleteViewModel;
import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
import com.example.onlinestore.domain.models.view.products.ProductEditViewModel;
import com.example.onlinestore.domain.models.view.products.ProductViewModel;
import com.example.onlinestore.services.category.CategoryService;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import com.example.onlinestore.services.product.ProductService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, CloudinaryService cloudinaryService, CategoryService categoryService, ModelMapper modelMapper) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add-product")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Add Product")
    public ModelAndView addProduct() {
        return view("/products/add-product");
    }

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProductConfirm(@ModelAttribute(name = "model") ProductAddBindingModel model) throws IOException {
        ProductServiceModel productServiceModel = this.modelMapper.map(model, ProductServiceModel.class);
        List<CategoryServiceModel> categories = this.categoryService.findAllCategories()
                .stream()
                .filter(categoryServiceModel -> model.getCategories().contains(categoryServiceModel.getId()))
                .collect(Collectors.toList());

        productServiceModel.setCategories(categories);
        productServiceModel.setImageUrl(
                this.cloudinaryService.uploadImage(model.getImage())
        );

        this.productService.addProduct(productServiceModel);

        return redirect("/products/all-products");
    }

    @GetMapping("/all-products")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("All Products")
    public ModelAndView allProducts(ModelAndView modelAndView) {
        List<ProductViewModel> productViewModels = this.productService.findAllProducts()
                .stream()
                .map(productServiceModel -> this.modelMapper.map(productServiceModel, ProductViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("products", productViewModels);

        return view("/products/all-products", modelAndView);
    }

    @GetMapping("/details-product/{id}")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Product Details")
    public ModelAndView detailsProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductDetailsViewModel productDetailsViewModel = this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class);

        modelAndView.addObject("product", productDetailsViewModel);

        return view("/products/details-product", modelAndView);
    }

    @GetMapping("/edit-product/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Edit Product")
    public ModelAndView editProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductEditViewModel productEditViewModel =
                this.modelMapper.map(productServiceModel, ProductEditViewModel.class);

        productEditViewModel.setCategories(
                productServiceModel.getCategories()
                        .stream()
                        .map(CategoryServiceModel::getName)
                        .collect(Collectors.toList()));

        modelAndView.addObject("product", productEditViewModel);

        return view("/products/edit-product", modelAndView);
    }

    @PatchMapping("/edit-product/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editProductConfirm(@PathVariable String id, @ModelAttribute(name = "model") ProductEditBindingModel model) {
        ProductServiceModel productServiceModel = this.modelMapper.map(model, ProductServiceModel.class);

        List<CategoryServiceModel> categories = model.getCategories()
                .stream()
                .map(categoryId -> {
                    CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
                    categoryServiceModel.setId(categoryId);

                    return categoryServiceModel;
                })
                .collect(Collectors.toList());

        productServiceModel.setCategories(categories);
        this.productService.editProduct(id, productServiceModel);

        return redirect("/products/all-products");
    }

    @GetMapping("/delete-product/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Delete Product")
    public ModelAndView deleteProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductDeleteViewModel productDeleteViewModel = this.modelMapper.map(productServiceModel, ProductDeleteViewModel.class);
        productDeleteViewModel.setCategories(productServiceModel.getCategories()
                .stream()
                .map(CategoryServiceModel::getName)
                .collect(Collectors.toList()));

        modelAndView.addObject("product", productDeleteViewModel);

        return view("/products/delete-product", modelAndView);
    }

    @DeleteMapping("/delete-product/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteProductConfirm(@PathVariable String id) {
        this.productService.deleteProduct(id);

        return redirect("/products/all-products/");
    }

    @GetMapping("/fetch/{category}")
    @ResponseBody
    public List<ProductViewModel> fetchByCategory(@PathVariable String category) {
        if(category.equals("all")) {
            return this.productService.findAllProducts()
                    .stream()
                    .map(product -> this.modelMapper.map(product, ProductViewModel.class))
                    .collect(Collectors.toList());
        }

        return this.productService.findAllByCategory(category)
                .stream()
                .map(product -> this.modelMapper.map(product, ProductViewModel.class))
                .collect(Collectors.toList());
    }

}
