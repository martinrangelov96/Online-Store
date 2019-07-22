package com.example.onlinestore.services;

import com.example.onlinestore.domain.models.service.OrderServiceModel;

import java.util.List;

public interface OrderService {

    OrderServiceModel createOrder(OrderServiceModel orderServiceModel);

    List<OrderServiceModel> findAllOrders();

    OrderServiceModel findOrderById(String id);

    List<OrderServiceModel> findAllOrdersByCustomer(String customerName);

    OrderServiceModel deleteOrder(OrderServiceModel orderServiceModel);
}
