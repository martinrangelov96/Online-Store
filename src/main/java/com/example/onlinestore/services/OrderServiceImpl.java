package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Order;
import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderServiceModel createOrder(OrderServiceModel orderServiceModel) {
        orderServiceModel.setOrderedOn(LocalDateTime.now());
        Order order = this.modelMapper.map(orderServiceModel, Order.class);

        this.orderRepository.save(order);

        return this.modelMapper.map(order, OrderServiceModel.class);
    }
}
