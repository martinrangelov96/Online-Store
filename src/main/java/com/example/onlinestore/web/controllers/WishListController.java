package com.example.onlinestore.web.controllers;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;
import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
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

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.PRODUCTS_ATTRIBUTE;

@Controller
@RequestMapping("/wishlist")
public class WishListController extends BaseController {

    private final WishListService wishListService;
    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public WishListController(WishListService wishListService, ProductService productService, UserService userService, ModelMapper modelMapper) {
        this.wishListService = wishListService;
        this.productService = productService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/customer-wishlist")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("My Wishlist")
    public ModelAndView wishlist(ModelAndView modelAndView, Principal principal) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        WishListServiceModel wishListServiceModel = this.wishListService.findAllProductsInWishlistByCustomer(userServiceModel);
        List<ProductServiceModel> productServiceModels = wishListServiceModel.getProducts();
        List<ProductDetailsViewModel> productDetailsViewModels = productServiceModels
                .stream()
                .map(productServiceModel -> this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject(PRODUCTS_ATTRIBUTE, productDetailsViewModels);
        return view("/wishlist/customer-wishlist", modelAndView);
    }

    @PostMapping("/add-to-wishlist")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addProductToWishlist(@RequestParam String productId, Principal principal) {
        ProductServiceModel productServiceModel = this.productService.findProductById(productId);
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());

        this.wishListService.addProductToWishlist(productServiceModel, userServiceModel);
        return redirect("/products/details-product/" + productId);
    }

    @DeleteMapping("/remove-from-wishlist")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeProductFromWishlist(@RequestParam String productId, Principal principal) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        this.wishListService.removeProductById(productId, userServiceModel);

        return redirect("/wishlist/customer-wishlist");
    }

}
