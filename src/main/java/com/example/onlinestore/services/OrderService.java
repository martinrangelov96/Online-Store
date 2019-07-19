package com.example.onlinestore.services;

import com.example.onlinestore.domain.models.service.OrderServiceModel;

public interface OrderService {

    OrderServiceModel createOrder(OrderServiceModel orderServiceModel);

}
