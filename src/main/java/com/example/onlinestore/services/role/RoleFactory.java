package com.example.onlinestore.services.role;

import com.example.onlinestore.domain.entities.Role;

import javax.management.relation.RoleNotFoundException;

import static com.example.onlinestore.constants.Constants.*;

class RoleFactory {

    private RoleFactory() {

    }

    static Role createRole(String roleType) {
        switch (roleType) {
            case ROLE_USER: return new Role(ROLE_USER);
            case ROLE_MODERATOR: return new Role(ROLE_MODERATOR);
            case ROLE_ADMIN: return new Role(ROLE_ADMIN);
            case ROLE_ROOT: return new Role(ROLE_ROOT);

            default:
                try {
                    throw new RoleNotFoundException(ROLE_NOT_FOUND_EXCEPTION_MESSAGE);
                } catch (RoleNotFoundException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

}
