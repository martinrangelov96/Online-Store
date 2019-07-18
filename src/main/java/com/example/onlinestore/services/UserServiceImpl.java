package com.example.onlinestore.services;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

        this.userRepository.save(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    @Override
    public UserServiceModel findUserById(String id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Username with this id does not exist!"));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);

        return userServiceModel;
    }

    @Override
    public UserServiceModel findUserByUsername(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);

        return userServiceModel;
    }

    @Override
    public UserServiceModel editServiceProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        if (!this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password!");
        }

        user.setPassword(!"".equals(userServiceModel.getPassword()) ?
                this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()) :
                user.getPassword()
        );
        user.setEmail(userServiceModel.getEmail());
        this.userRepository.save(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        List<UserServiceModel> allUsers = this.userRepository.findAll()
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
}
