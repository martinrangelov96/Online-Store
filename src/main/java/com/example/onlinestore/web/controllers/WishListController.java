package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;
import com.example.onlinestore.services.product.ProductService;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.services.wishlist.WishListService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/wishlist")
public class WishListController extends BaseController {

    private final WishListService wishListService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public WishListController(WishListService wishListService, ProductService productService, UserService userService) {
        this.wishListService = wishListService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/customer-wishlist")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("My Wishlist")
    public ModelAndView wishlist() {
        return view("/wishlist/customer-wishlist");
    }

    @PostMapping("/add-to-wishlist")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addProductToWishlist(@RequestParam String productId, Principal principal) {
        ProductServiceModel productServiceModel = this.productService.findProductById(productId);
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());

        this.wishListService.addProductToWishlist(productServiceModel, userServiceModel);
        return redirect("/products/details-product/" + productId);
    }

}
