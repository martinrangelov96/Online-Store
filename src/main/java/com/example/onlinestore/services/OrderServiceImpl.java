package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Order;
import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.errors.OrderNotFoundException;
import com.example.onlinestore.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<OrderServiceModel> findAllOrders() {
        List<OrderServiceModel> orderServiceModels = this.orderRepository.findAll()
                .stream()
                .map(order -> this.modelMapper.map(order, OrderServiceModel.class))
                .collect(Collectors.toList());

        return orderServiceModels;
    }

    @Override
    public OrderServiceModel findOrderById(String id) {
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with this id does not exist!"));

        return this.modelMapper.map(order, OrderServiceModel.class);
    }

    @Override
    public List<OrderServiceModel> findAllOrdersByCustomer(String customerName) {
        List<OrderServiceModel> orderServiceModels = this.orderRepository.findOrderByCustomer_UsernameOrderByOrderedOn(customerName)
                .stream()
                .map(order -> this.modelMapper.map(order, OrderServiceModel.class))
                .collect(Collectors.toList());

        return orderServiceModels;
    }

    @Override
    public OrderServiceModel deleteOrder(OrderServiceModel orderServiceModel) {
        Order order = this.orderRepository.findById(orderServiceModel.getId())
                .orElseThrow(() -> new OrderNotFoundException("Order with this id does not exist!"));

        this.orderRepository.delete(order);

        return this.modelMapper.map(order, OrderServiceModel.class);
    }
}
