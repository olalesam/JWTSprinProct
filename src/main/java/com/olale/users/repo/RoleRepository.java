package com.olale.users.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.olale.users.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Recherche par nom du rôle (car ton champ s'appelle roleName)
    Role findByRoleName(String roleName);
}
