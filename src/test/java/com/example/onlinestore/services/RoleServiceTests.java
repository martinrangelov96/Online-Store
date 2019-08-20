package com.example.onlinestore.services;

import com.example.onlinestore.domain.entities.Role;
import com.example.onlinestore.domain.models.service.RoleServiceModel;
import com.example.onlinestore.repository.RoleRepository;
import com.example.onlinestore.services.role.RoleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoleServiceTests {

    @Autowired
    private RoleService roleService;

    @MockBean
    private RoleRepository mockRoleRepository;

    private List<Role> roles;

    @Before
    public void setupTest() {
        this.roles = new ArrayList<>();
        when(this.mockRoleRepository.findAll())
                .thenReturn(this.roles);
    }

    @Test
    public void whenRepositoryIsEmpty_seedAll4Roles_successfully() {
        this.roles.clear();
        this.roleService.seedRolesInDb();
        //verify saving roles 4 times
        verify(this.mockRoleRepository, times(4)).save(any());
    }

//    @Test
//    public void whenRepositoryIsNotEmpty_dontSeed() {
//        Role role = new Role();
//        role.setAuthority("ADMIN");
//        this.mockRoleRepository.save(role);
//
//        this.roleService.seedRolesInDb();
//        verify(this.mockRoleRepository, times(0)).save(any());
//    }

    @Test
    public void findAllRoles_when1Role1_return1Role() {
        Role role = new Role();
        role.setAuthority("ADMIN");

        this.roles.add(role);

        Set<RoleServiceModel> actualRoles = this.roleService.findAllRoles();
        Optional<RoleServiceModel> actualRole = actualRoles.stream().findFirst();

        assertEquals("ADMIN", actualRole.get().getAuthority());
        assertEquals(1, actualRoles.size());
    }

    @Test
    public void findAllRoles_whenNoRoles_returnEmptyList() {
        Set<RoleServiceModel> actualRoles = this.roleService.findAllRoles();
        assertEquals(0, actualRoles.size());
    }

//    @Test
//    public void findByAuthority_whenValidAuthority_returnNotNullRole() {
//        Role role = new Role();
//        role.setAuthority("ADMIN");
//
//        when(this.mockRoleRepository.findById("ADMIN"))
//                .thenReturn(Optional.of(role));
//
//        this.roles.add(role);
//
//        RoleServiceModel foundRole = this.roleService.findByAuthority("ADMIN");
//        assertNotNull(foundRole);
//    }

}
