package com.example.onlinestore.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Wishlist Not Found")
public class WishListNotFoundException extends RuntimeException {

    public WishListNotFoundException() {
    }

    public WishListNotFoundException(String message) {
        super(message);
    }
}
