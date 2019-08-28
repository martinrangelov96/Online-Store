package com.example.onlinestore.repository;

import com.example.onlinestore.domain.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, String> {

    Optional<WishList> findByCustomer_Id(String customerId);

    WishList findAllByCustomer_Id(String customerId);

}
