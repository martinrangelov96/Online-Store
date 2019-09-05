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

import static com.example.onlinestore.constants.Constants.*;
import static com.example.onlinestore.constants.OrderConstants.*;

@Controller
@RequestMapping(REQUEST_MAPPING_ORDERS_CONST)
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(ALL_ORDERS_GET)
    @PreAuthorize(HAS_ROLE_ADMIN)
    @PageTitle(ALL_ORDERS_PAGE_TITLE)
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

        if (orderViewModels.isEmpty()) {
            modelAndView.addObject(EMPTY_LIST_NAME, EMPTY_MODERATOR_ORDERS_MESSAGE);
        }

        modelAndView.addObject(ORDERS_ATTRIBUTE, orderViewModels);
        return view(LIST_ORDERS_VIEW_NAME, modelAndView);
    }

    @GetMapping(CUSTOMER_ORDERS_GET)
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(CUSTOMER_ORDERS_PAGE_TITLE)
    public ModelAndView customerOrders(Principal principal, ModelAndView modelAndView) {
        List<OrderServiceModel> orderServiceModels =
                this.orderService.findAllOrdersByCustomerOrderedByTime(principal.getName());
        List<OrderViewModel> orderViewModels = orderServiceModels
                .stream()
                .map(orderServiceModel -> this.modelMapper.map(orderServiceModel, OrderViewModel.class))
                .collect(Collectors.toList());

        if (orderViewModels.isEmpty()) {
            modelAndView.addObject(EMPTY_LIST_NAME, String.format(EMPTY_LIST_PLURAL_MESSAGE, ORDERS_CONST));
        }

        modelAndView.addObject(ORDERS_ATTRIBUTE, orderViewModels);
        return view(LIST_ORDERS_VIEW_NAME, modelAndView);
    }

    @GetMapping(DETAILS_ORDER_BY_ID_GET)
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(ORDER_DETAILS_PAGE_TITLE)
    public ModelAndView allOrderDetails(@PathVariable String id, ModelAndView modelAndView) {
        OrderServiceModel orderServiceModel = this.orderService.findOrderById(id);
        OrderViewModel orderViewModel = this.modelMapper.map(orderServiceModel, OrderViewModel.class);

        modelAndView.addObject(ORDER_ATTRIBUTE, orderViewModel);
        return view(DETAILS_ORDER_VIEW_NAME, modelAndView);
    }

}
