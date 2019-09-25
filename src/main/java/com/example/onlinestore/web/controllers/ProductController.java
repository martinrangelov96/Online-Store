package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.binding.ProductAddBindingModel;
import com.example.onlinestore.domain.models.binding.ProductEditBindingModel;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.view.products.ProductDeleteViewModel;
import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
import com.example.onlinestore.domain.models.view.products.ProductEditViewModel;
import com.example.onlinestore.domain.models.view.products.ProductViewModel;
import com.example.onlinestore.services.category.CategoryService;
import com.example.onlinestore.services.product.ProductService;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.services.wishlist.WishListService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;
import static com.example.onlinestore.constants.ProductConstants.*;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final WishListService wishListService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, UserService userService, WishListService wishListService, ModelMapper modelMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.wishListService = wishListService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add-product")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(ADD_PRODUCT_PAGE_TITLE)
    public ModelAndView addProduct() {
        return view("/products/add-product");
    }

    @PostMapping("/add-product")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView addProductConfirm(@ModelAttribute(name = MODEL_ATTRIBUTE) ProductAddBindingModel model) throws IOException {
        ProductServiceModel productServiceModel = this.modelMapper.map(model, ProductServiceModel.class);

        List<CategoryServiceModel> categories = this.categoryService.findAllCategories()
                .stream()
                .filter(categoryServiceModel -> model.getCategories().contains(categoryServiceModel.getId()))
                .collect(Collectors.toList());

        productServiceModel.setCategories(categories);

        this.productService.addProduct(productServiceModel);
        return redirect("/products/all-products");
    }

    @GetMapping("/all-products")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(ALL_PRODUCTS_PAGE_TITLE)
    public ModelAndView allProducts(ModelAndView modelAndView) {
        List<ProductViewModel> productViewModels = this.productService.findAllProducts()
                .stream()
                .map(productServiceModel -> this.modelMapper.map(productServiceModel, ProductViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject(PRODUCTS_ATTRIBUTE, productViewModels);
        return view("/products/all-products", modelAndView);
    }

    @GetMapping("/details-product/{id}")
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(PRODUCT_DETAILS_PAGE_TITLE)
    public ModelAndView detailsProduct(@PathVariable String id, Principal principal, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductDetailsViewModel productDetailsViewModel = this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class);
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());

        if (this.wishListService.checkIfProductExists(productServiceModel, userServiceModel)) {
            modelAndView.addObject(PRODUCT_ATTRIBUTE, productDetailsViewModel);
            modelAndView.addObject(ADDED_ATTRIBUTE, String.format(ADDED_MESSAGE, productDetailsViewModel.getName()));
            return view("/products/details-product", modelAndView);
        }

        modelAndView.addObject(PRODUCT_ATTRIBUTE, productDetailsViewModel);
        return view("/products/details-product", modelAndView);
    }

    @PostMapping("/add-to-wishlist")
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(PRODUCT_DETAILS_PAGE_TITLE)
    public ModelAndView addProductToWishlist(@RequestParam String productId, Principal principal, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(productId);
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());

        if (this.wishListService.checkIfProductExists(productServiceModel, userServiceModel)) {
            return this.detailsProduct(productId, principal, modelAndView);
        }

        this.wishListService.addProductToWishlist(productServiceModel, userServiceModel);
        return redirect("/products/details-product/" + productId);
    }

    @GetMapping("/edit-product/{id}")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(EDIT_PRODUCT_PAGE_TITLE)
    public ModelAndView editProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductEditViewModel productEditViewModel =
                this.modelMapper.map(productServiceModel, ProductEditViewModel.class);

        productEditViewModel.setCategories(
                productServiceModel.getCategories()
                        .stream()
                        .map(CategoryServiceModel::getName)
                        .collect(Collectors.toList()));

        modelAndView.addObject(PRODUCT_ATTRIBUTE, productEditViewModel);
        return view("/products/edit-product", modelAndView);
    }

    @PatchMapping("/edit-product/{id}")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView editProductConfirm(@PathVariable String id, @ModelAttribute(name = MODEL_ATTRIBUTE) ProductEditBindingModel model) throws IOException {
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
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(DELETE_PRODUCT_PAGE_TITLE)
    public ModelAndView deleteProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductDeleteViewModel productDeleteViewModel = this.modelMapper.map(productServiceModel, ProductDeleteViewModel.class);

        productDeleteViewModel.setCategories(productServiceModel.getCategories()
                .stream()
                .map(CategoryServiceModel::getName)
                .collect(Collectors.toList()));

        modelAndView.addObject(PRODUCT_ATTRIBUTE, productDeleteViewModel);
        return view("/products/delete-product", modelAndView);
    }

    @DeleteMapping("/delete-product/{id}")
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView deleteProductConfirm(@PathVariable String id) {
        this.productService.deleteProduct(id);
        return redirect("/products/all-products");
    }

    @GetMapping("/fetch/{category}")
    @ResponseBody
    public List<ProductViewModel> fetchByCategory(@PathVariable String category) {
        if(category.equals(ALL_CONST)) {
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
