package com.example.onlinestore.repository;

import com.example.onlinestore.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product AS p " +
            "JOIN p.categories AS c " +
            "WHERE c.name = :category")
    List<Product> findAllProductsByCategory(@Param("category") String category);

}
