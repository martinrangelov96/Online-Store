package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Role;
import com.example.onlinestore.domain.models.service.RoleServiceModel;
import com.example.onlinestore.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedRolesInDb() {
        if (this.roleRepository.count() == 0) {
            this.roleRepository.save(new Role(ROLE_USER));
            this.roleRepository.save(new Role(ROLE_MODERATOR));
            this.roleRepository.save(new Role(ROLE_ADMIN));
            this.roleRepository.save(new Role(ROLE_ROOT));
        }
    }

    @Override
    public Set<RoleServiceModel> findAllRoles() {
        Set<RoleServiceModel> roles = this.roleRepository.findAll()
                .stream()
                .map(role -> this.modelMapper.map(role, RoleServiceModel.class))
                .collect(Collectors.toSet());

        return roles;
    }

    @Override
    public RoleServiceModel findByAuthority(String authority) {
        RoleServiceModel roleServiceModel =
                this.modelMapper.map(this.roleRepository.findByAuthority(authority), RoleServiceModel.class);

        return roleServiceModel;
    }
}
