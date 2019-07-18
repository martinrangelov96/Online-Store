package com.example.onlinestore.services;

import com.example.onlinestore.domain.models.service.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserById(String id);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editServiceProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

}
