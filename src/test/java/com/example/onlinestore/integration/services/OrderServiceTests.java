package com.example.onlinestore.integration.services;

import com.example.onlinestore.domain.entities.Order;
import com.example.onlinestore.domain.entities.Product;
import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.service.OrderServiceModel;
import com.example.onlinestore.repository.OrderRepository;
import com.example.onlinestore.services.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository mockOrderRepository;

    private List<Order> orders;

    @Before
    public void setupTest() {
        this.orders = new ArrayList<>();
        when(this.mockOrderRepository.findAllByOrderByOrderedOn())
                .thenReturn(this.orders);
    }

    @Test
    public void findAllOrders_when1Orders_return1Order() {
        String username = "Test username";
        String productImageUrl = "http://image.url";
        String productName = "product";
        BigDecimal productPrice = BigDecimal.valueOf(1.34);

        Order order = new Order();
        order.setCustomer(new User(){{setUsername(username);}});
        order.setProducts(new ArrayList<>(){{
            add(new Product(){{
                setImageUrl(productImageUrl);
                setName(productName);
                setPrice(productPrice);
            }});
        }});

        this.orders.add(order);

        List<OrderServiceModel> result = this.orderService.findAllOrdersOrderedByDate();
        OrderServiceModel orderResult = result.get(0);

        assertEquals(1, result.size());
        assertEquals(username, orderResult.getCustomer().getUsername());
        assertEquals(productImageUrl, orderResult.getProducts().get(0).getImageUrl());
        assertEquals(productName, orderResult.getProducts().get(0).getName());
        assertEquals(productPrice, orderResult.getProducts().get(0).getPrice());
    }

    @Test
    public void findAllOrders_whenNoOrders_returnEmptyOrders() {
        orders.clear();
        List<OrderServiceModel> result = this.orderService.findAllOrdersOrderedByDate();
        assertTrue(result.isEmpty());
    }

}
