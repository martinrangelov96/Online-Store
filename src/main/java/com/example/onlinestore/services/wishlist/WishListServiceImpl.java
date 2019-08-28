package com.example.onlinestore.services.wishlist;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.entities.WishList;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;
import com.example.onlinestore.errors.WishListNotFoundException;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.repository.WishListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.onlinestore.constants.Constants.WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE;


@Service
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public WishListServiceImpl(WishListRepository wishListRepository, ModelMapper modelMapper) {
        this.wishListRepository = wishListRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WishListServiceModel addProductToWishlist(ProductServiceModel productServiceModel,
                                                     UserServiceModel userServiceModel) {

        WishList wishList = this.wishListRepository.findByCustomer_Id(userServiceModel.getId())
                .orElseThrow(() -> new WishListNotFoundException(WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE));
        wishList.getProducts().add(this.modelMapper.map(productServiceModel, Product.class));

        this.wishListRepository.save(wishList);
        return this.modelMapper.map(wishList, WishListServiceModel.class);
    }
}

