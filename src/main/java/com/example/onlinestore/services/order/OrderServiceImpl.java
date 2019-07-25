package com.example.onlinestore.services.order;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.Order;
import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.errors.OrderNotFoundException;
import com.example.onlinestore.repository.OrderRepository;
import com.example.onlinestore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
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
        List<OrderServiceModel> orderServiceModels = this.orderRepository.findAllByOrderByOrderedOn()
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
    public OrderServiceModel deleteOrder(OrderServiceModel orderServiceModel, UserServiceModel userServiceModel, BigDecimal orderTotalPrice) {
        Order order = this.orderRepository.findById(orderServiceModel.getId())
                .orElseThrow(() -> new OrderNotFoundException("Order with this id does not exist!"));

        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));
        BigDecimal userBalance = user.getBalance();

        this.orderRepository.delete(order);
        user.setBalance(userBalance.add(orderTotalPrice));
        this.userRepository.save(user);

        return this.modelMapper.map(order, OrderServiceModel.class);
    }
}
