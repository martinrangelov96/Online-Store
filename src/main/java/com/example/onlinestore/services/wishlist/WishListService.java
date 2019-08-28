package com.example.onlinestore.services.wishlist;

import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;

public interface WishListService {

    WishListServiceModel addProductToWishlist(ProductServiceModel productServiceModel,
                                              UserServiceModel userServiceModel);

    WishListServiceModel findAllProductsInWishlistByCustomer(UserServiceModel userServiceModel);

    WishListServiceModel removeProductById(String productId, UserServiceModel userServiceModel);

}
