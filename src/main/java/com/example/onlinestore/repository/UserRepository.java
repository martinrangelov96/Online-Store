package com.example.onlinestore.repository;

import com.example.onlinestore.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findAllByOrderByRegisteredOn();

    Optional<User> findByUsername(String username);
}
