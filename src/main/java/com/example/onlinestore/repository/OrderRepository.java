package com.example.onlinestore.repository;

import com.example.onlinestore.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findAllByOrderByOrderedOn();

    List<Order> findOrderByCustomer_UsernameOrderByOrderedOn(String username);

}
