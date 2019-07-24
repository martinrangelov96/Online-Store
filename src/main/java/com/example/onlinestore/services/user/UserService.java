package com.example.onlinestore.services.user;

import com.example.onlinestore.domain.models.service.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editServiceProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

    void setRole(String id, String role);

}
