package com.olale.users.repo;

import com.olale.users.entities.Role;

public interface RoleRepository {

    Role findByRole(String role);

}
