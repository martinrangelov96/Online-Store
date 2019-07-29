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
    private final static String EMPTY_SHOPPING_CART_MESSAGE = "Your shopping cart is empty!";
    private final static String NOT_ENOUGH_MONEY_MESSAGE = "You don't have enough money for this order!";

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
        this.productService.updateQuantityAfterAddingToCart(id, quantity);
        ProductDetailsViewModel productDetailsViewModel =
                this.modelMapper.map(productServiceModel, ProductDetailsViewModel.class);

        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(productDetailsViewModel);
        cartItem.setQuantity(quantity);

        var cart = this.retrieveCart(session);
        this.addItemToCart(cartItem, cart);

        return redirect("/users/home");
    }

    @GetMapping("/details-cart")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Cart Details")
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session, Principal principal) {
        var cart = this.retrieveCart(session);
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        BigDecimal orderTotalPrice = this.calculateTotalPrice(cart);

        if (cart.size() == 0) {
            modelAndView.addObject("emptyCartMessage", EMPTY_SHOPPING_CART_MESSAGE);
        }
        if (!(userServiceModel.getBalance().compareTo(orderTotalPrice) >= 0)) {
            modelAndView.addObject("notEnoughMoneyMessage", NOT_ENOUGH_MONEY_MESSAGE);
        }
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
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        BigDecimal orderTotalPrice = this.calculateTotalPrice(cart);

        if (cart.size() == 0) {
            return redirect("/cart/details-cart");
        }

        if (!(userServiceModel.getBalance().compareTo(orderTotalPrice) >= 0)) {
            return redirect("/cart/details-cart");
        }

        OrderServiceModel orderServiceModel = this.prepareOrder(cart, principal.getName());

        this.orderService.createOrder(orderServiceModel);
        this.userService.updateMoneyAfterCheckout(userServiceModel, orderTotalPrice);
        cart.clear();

        return redirect("/users/home");
    }

    @SuppressWarnings("unchecked")
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
        cart.forEach(ci -> {
            if (ci.getProduct().getId().equals(id)) {
                this.productService.updateQuantityAfterRemovingFromCart(ci.getProduct().getId(), ci.getQuantity());
            }
        });
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

        List<ProductServiceModel> productServiceModels = new ArrayList<>();

        for (ShoppingCartItem item : cart) {
            ProductServiceModel productServiceModel = this.modelMapper.map(item.getProduct(), ProductServiceModel.class);
            for (int i = 0; i < item.getQuantity(); i++) {
                productServiceModels.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(productServiceModels);
        BigDecimal totalPrice = this.calculateTotalPrice(cart);
        orderServiceModel.setTotalPrice(totalPrice);

        return orderServiceModel;
    }

}
