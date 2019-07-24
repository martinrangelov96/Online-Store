package com.example.onlinestore.services.user;

import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.repository.UserRepository;
import com.example.onlinestore.services.role.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        if (this.userRepository.count() == 0) {
            this.roleService.seedRolesInDb();
            userServiceModel.setAuthorities(this.roleService.findAllRoles());
        } else {
            userServiceModel.setAuthorities(new HashSet<>());
            userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_USER));
        }

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setRegisteredOn(LocalDateTime.now());
        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

        this.userRepository.save(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public UserServiceModel findUserByUsername(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);

        return userServiceModel;
    }

    @Override
    public UserServiceModel editServiceProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));

        user.setPassword(userServiceModel.getPassword() != null ?
                this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()) :
                user.getPassword()
        );
        user.setEmail(userServiceModel.getEmail());
        if (userServiceModel.getImageUrl() != null) {
            user.setImageUrl(userServiceModel.getImageUrl());
        }
        this.userRepository.save(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        List<UserServiceModel> allUsers = this.userRepository.findAllByOrderByRegisteredOn()
                .stream()
                .map(user -> this.modelMapper.map(user, UserServiceModel.class))
                .collect(Collectors.toList());

        return allUsers;
    }

    @Override
    public void setRole(String id, String role) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find user with this id!"));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);
        userServiceModel.getAuthorities().clear();

        switch (role) {
            case USER_STRING:
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_USER));
                break;
            case MODERATOR_STRING:
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_USER));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_MODERATOR));
                break;
            case ADMIN_STRING:
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_USER));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_MODERATOR));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority(ROLE_ADMIN));
                break;
        }

        user = this.modelMapper.map(userServiceModel, User.class);

        this.userRepository.save(user);
    }

    @Override
    public UserServiceModel addMoneyToBalance(UserServiceModel userServiceModel, BigDecimal moneyToAdd) {
        User user = this.modelMapper.map(userServiceModel, User.class);

        BigDecimal currentBalance = user.getBalance();
        user.setBalance(currentBalance.add(moneyToAdd));

        this.userRepository.save(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }
}
