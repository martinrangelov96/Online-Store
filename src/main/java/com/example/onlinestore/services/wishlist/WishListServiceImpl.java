package com.example.onlinestore.services.wishlist;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.entities.WishList;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.service.WishListServiceModel;
import com.example.onlinestore.errors.WishListNotFoundException;
import com.example.onlinestore.repository.WishListRepository;
import com.example.onlinestore.services.product.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.onlinestore.constants.Constants.WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE;


@Service
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public WishListServiceImpl(WishListRepository wishListRepository, ProductService productService, ModelMapper modelMapper) {
        this.wishListRepository = wishListRepository;
        this.productService = productService;
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

    @Override
    public WishListServiceModel findAllProductsInWishlistByCustomer(UserServiceModel userServiceModel) {
        WishList customerWishlistProducts = this.wishListRepository.findAllByCustomer_Id(userServiceModel.getId());

        return this.modelMapper.map(customerWishlistProducts, WishListServiceModel.class);
    }

    @Override
    public WishListServiceModel removeProductById(String productId, UserServiceModel userServiceModel) {
        WishList wishList = this.wishListRepository.findByCustomer_Id(userServiceModel.getId())
                .orElseThrow(() -> new WishListNotFoundException(WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE));
        ProductServiceModel productServiceModel = this.productService.findProductById(productId);
        Product product = this.modelMapper.map(productServiceModel, Product.class);
        wishList.getProducts().removeIf(product1 -> product1.getId().equals(product.getId()));

        this.wishListRepository.save(wishList);
        return this.modelMapper.map(wishList, WishListServiceModel.class);
    }

    @Override
    public boolean checkIfProductExists(ProductServiceModel productServiceModel, UserServiceModel userServiceModel) {
        WishList customerWishlist = this.wishListRepository.findByCustomer_Id(userServiceModel.getId())
                .orElseThrow(() -> new WishListNotFoundException(WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE));
        if (customerWishlist.getProducts().stream().anyMatch(product -> product.getId().equals(productServiceModel.getId()))){
            return true;
        }

        return false;
    }
}

