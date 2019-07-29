package com.example.onlinestore.web.custom;

import com.example.onlinestore.domain.models.view.cart.ShoppingCartItem;
import com.example.onlinestore.services.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.example.onlinestore.constants.Constants.SHOPPING_CART;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final ProductService productService;

    @Autowired
    public CustomLogoutHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        List<ShoppingCartItem> cart = (List<ShoppingCartItem>) httpServletRequest.getSession().getAttribute(SHOPPING_CART);
        if (cart != null) {
            cart.forEach(cartItem -> this.productService.updateQuantityAfterRemovingFromCart(
                    cartItem.getProduct().getId(), cartItem.getQuantity()));
        }
    }
}
