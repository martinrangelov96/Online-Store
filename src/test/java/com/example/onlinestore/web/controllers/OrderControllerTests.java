package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.entities.Order;
import com.example.onlinestore.domain.models.view.orders.OrderViewModel;
import com.example.onlinestore.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderControllerTests {

    @Autowired
    private OrderController orderController;

    @MockBean
    private Principal principal;

    @MockBean
    private OrderRepository mockOrderRepository;

    private ArrayList<Order> orders;

    @Before
    public void setupTest() {
        this.orders = new ArrayList<>();

        when(this.mockOrderRepository.findOrderByCustomer_UsernameOrderByOrderedOn(any()))
                .thenReturn(this.orders);
    }

    @Test
    @WithMockUser
    public void customerOrders_whenCustomerHasNoOrders_returnEmpty() {
        this.orders.clear();
        ModelAndView modelAndView = new ModelAndView();
        when(this.principal.getName())
                .thenReturn("");
        ModelAndView result = this.orderController.customerOrders(this.principal, modelAndView);

        List<OrderViewModel> orderViewModels = (List<OrderViewModel>) result.getModelMap().get("orders");
        assertTrue(orderViewModels.isEmpty());
    }

    @Test
    @WithMockUser
    public void customerOrders_whenAllOrdersAreForCustomer_returnOrders() {
        this.orders.addAll(List.of(
                new Order()
        ));

        ModelAndView modelAndView = new ModelAndView();
        when(this.principal.getName())
                .thenReturn("");
        ModelAndView result = this.orderController.customerOrders(this.principal, modelAndView);

        List<OrderViewModel> orderViewModels = (List<OrderViewModel>) result.getModelMap().get("orders");
        assertEquals(this.orders.size(), orderViewModels.size());
    }

    @Test
    @WithMockUser
    public void customerOrders_whenNotAllOrdersAreForCustomer_returnOrders() {
        this.orders.addAll(List.of(
                new Order()
        ));

        ModelAndView modelAndView = new ModelAndView();
        when(this.principal.getName())
                .thenReturn("");
        ModelAndView result = this.orderController.customerOrders(this.principal, modelAndView);

        List<OrderViewModel> orderViewModels = (List<OrderViewModel>) result.getModelMap().get("orders");
        assertEquals(this.orders.size(), orderViewModels.size());
    }
}
