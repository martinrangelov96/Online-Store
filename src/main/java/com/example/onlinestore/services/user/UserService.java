package com.example.onlinestore.services.user;

import com.example.onlinestore.domain.models.service.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;
import java.util.List;

public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsersOrderedByDate();

    void setRole(String id, String role);

    void existsByUsername(String username);

    void existsByEmail(String email);

    UserServiceModel addMoneyToBalance(UserServiceModel userServiceModel, BigDecimal moneyToAdd);

    UserServiceModel updateMoneyAfterCheckout(UserServiceModel userServiceModel, BigDecimal orderTotalPrice);
}
