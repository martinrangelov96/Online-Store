package com.example.onlinestore.services.order;

import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    OrderServiceModel createOrder(OrderServiceModel orderServiceModel);

    List<OrderServiceModel> findAllOrders();

    OrderServiceModel findOrderById(String id);

    List<OrderServiceModel> findAllOrdersByCustomer(String customerName);

//    OrderServiceModel deleteOrder(OrderServiceModel orderServiceModel, UserServiceModel userServiceModel, BigDecimal orderTotalPrice);
}
