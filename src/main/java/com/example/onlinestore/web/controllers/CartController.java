package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.domain.models.service.ProductServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.view.cart.ShoppingCartItem;
import com.example.onlinestore.domain.models.view.products.ProductDetailsViewModel;
import com.example.onlinestore.services.order.OrderService;
import com.example.onlinestore.services.product.ProductService;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController extends BaseController {

    private final static String SHOPPING_CART = "shopping-cart";

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public CartController(ProductService productService, UserService userService, OrderService orderService, ModelMapper modelMapper) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addToCartConfirm(String id, int quantity, HttpSession session) {
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        ProductDetailsViewModel product =
                this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class);

        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        var cart = this.retrieveCart(session);
        this.addItemToCart(cartItem, cart);

        return redirect("/users/home");
    }

    @GetMapping("/details-cart")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Cart Details")
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session) {
        var cart = this.retrieveCart(session);
        modelAndView.addObject("totalPrice", this.calculateTotalPrice(cart));

        return view("/cart/details-cart", modelAndView);
    }

    @DeleteMapping("/remove-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeFromCartConfirm(String id, HttpSession session) {
        var cart = this.retrieveCart(session);

        this.removeItemFromCart(id, cart);

        return redirect("/cart/details-cart");
    }

    @PostMapping("/checkout-cart")
    public ModelAndView checkoutConfirm(HttpSession session, Principal principal) {
        var cart = this.retrieveCart(session);
        String customer = principal.getName();
        UserServiceModel userServiceModel = this.userService.findUserByUsername(customer);
        BigDecimal orderTotalPrice = this.calculateTotalPrice(cart);

        if (!(userServiceModel.getBalance().compareTo(orderTotalPrice) >= 0)) {
            return redirect("/cart/details-cart");
        }

        OrderServiceModel orderServiceModel = this.prepareOrder(cart, customer);

        this.orderService.createOrder(orderServiceModel);
        this.userService.updateMoneyAfterCheckout(userServiceModel, orderTotalPrice);
        cart.clear();

        return redirect("/users/home");
    }

    private List<ShoppingCartItem> retrieveCart(HttpSession session) {
        this.initCart(session);

        return (List<ShoppingCartItem>) session.getAttribute(SHOPPING_CART);
    }

    private void initCart(HttpSession session) {
        if (session.getAttribute(SHOPPING_CART) == null) {
            session.setAttribute(SHOPPING_CART, new LinkedList<>());
        }
    }

    private void addItemToCart(ShoppingCartItem item, List<ShoppingCartItem> cart) {
        for (ShoppingCartItem shoppingCartItem : cart) {
            if (shoppingCartItem.getProduct().getId().equals(item.getProduct().getId())) {
                shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        cart.add(item);
    }

    private void removeItemFromCart(String id, List<ShoppingCartItem> cart) {
        cart.removeIf(ci -> ci.getProduct().getId().equals(id));
    }

    private BigDecimal calculateTotalPrice(List<ShoppingCartItem> cartItems) {
        BigDecimal totalPrice = new BigDecimal(0);

        for (ShoppingCartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return totalPrice;
    }

    private OrderServiceModel prepareOrder(List<ShoppingCartItem> cart, String customer) {
        OrderServiceModel orderServiceModel = new OrderServiceModel();
        UserServiceModel userServiceModel = this.userService.findUserByUsername(customer);
        orderServiceModel.setCustomer(userServiceModel);

        List<ProductServiceModel> products = new ArrayList<>();

        for (ShoppingCartItem item : cart) {
            ProductServiceModel productServiceModel = this.modelMapper.map(item.getProduct(), ProductServiceModel.class);
            for (int i = 0; i < item.getQuantity(); i++) {
                products.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(products);
        BigDecimal totalPrice = this.calculateTotalPrice(cart);
        orderServiceModel.setTotalPrice(totalPrice);

        return orderServiceModel;
    }

}
