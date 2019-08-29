package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;
import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.services.wishlist.WishListService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;

@Controller
@RequestMapping("/wishlist")
public class WishListController extends BaseController {

    private final WishListService wishListService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public WishListController(WishListService wishListService, UserService userService, ModelMapper modelMapper) {
        this.wishListService = wishListService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/customer-wishlist")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("My Wishlist")
    public ModelAndView customerWishlist(ModelAndView modelAndView, Principal principal) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        WishListServiceModel wishListServiceModel = this.wishListService.findAllProductsInWishlistByCustomer(userServiceModel);
        Set<ProductServiceModel> productServiceModels = wishListServiceModel.getProducts();
        Set<ProductDetailsViewModel> productDetailsViewModels = productServiceModels
                .stream()
                .map(productServiceModel -> this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class))
                .collect(Collectors.toSet());

        if (productDetailsViewModels.isEmpty()) {
            modelAndView.addObject(EMPTY_LIST_NAME, String.format(EMPTY_LIST_SINGULAR_MESSAGE, WISHLIST_CONST));
        }

        modelAndView.addObject(PRODUCTS_ATTRIBUTE, productDetailsViewModels);
        return view("/wishlist/customer-wishlist", modelAndView);
    }

    @DeleteMapping("/remove-from-wishlist")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeProductFromWishlist(@RequestParam String productId, Principal principal) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        this.wishListService.removeProductById(productId, userServiceModel);

        return redirect("/wishlist/customer-wishlist");
    }

}
