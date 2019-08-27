package com.example.onlinestore.web.controllers;

import com.example.onlinestore.web.annotations.PageTitle;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wishlist")
public class WishListController extends BaseController {

    @GetMapping("/customer-wishlist")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("My Wishlist")
    public ModelAndView addToWishList() {
        return view("/wishlist/customer-wishlist");
    }

    @PostMapping("/add-to-wishlist")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addToWishListConfirm() {
        return null;
    }

}
