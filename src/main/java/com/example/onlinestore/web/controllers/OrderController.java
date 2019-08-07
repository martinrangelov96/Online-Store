package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.domain.models.service.RoleServiceModel;
import com.example.onlinestore.domain.models.view.orders.OrderViewModel;
import com.example.onlinestore.services.order.OrderService;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("All Orders")
    public ModelAndView allOrders(ModelAndView modelAndView) {
        List<OrderServiceModel> orderServiceModels = this.orderService.findAllOrdersOrderedByDate();
        List<OrderViewModel> orderViewModels = orderServiceModels
                .stream()
                .map(orderServiceModel -> {
                    OrderViewModel orderViewModel = this.modelMapper.map(orderServiceModel, OrderViewModel.class);
                    orderViewModel.getCustomer().setAuthorities(orderServiceModel.getCustomer().getAuthorities()
                    .stream()
                    .map(RoleServiceModel::getAuthority)
                    .collect(Collectors.toList()));

                    return orderViewModel;
                })
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        return view("/orders/list-orders", modelAndView);
    }

    @GetMapping("/customer-orders")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Customer Orders")
    public ModelAndView customerOrders(Principal principal, ModelAndView modelAndView) {
        List<OrderServiceModel> orderServiceModels =
                this.orderService.findAllOrdersByCustomerOrderedByTime(principal.getName());
        List<OrderViewModel> orderViewModels = orderServiceModels
                .stream()
                .map(orderServiceModel -> this.modelMapper.map(orderServiceModel, OrderViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        return view("/orders/list-orders", modelAndView);
    }

    @GetMapping("/details-order/{id}")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Order Details")
    public ModelAndView allOrderDetails(@PathVariable String id, ModelAndView modelAndView) {
        OrderServiceModel orderServiceModel = this.orderService.findOrderById(id);
        OrderViewModel orderViewModel = this.modelMapper.map(orderServiceModel, OrderViewModel.class);

        modelAndView.addObject("order", orderViewModel);

        return view("/orders/details-order", modelAndView);
    }

}
