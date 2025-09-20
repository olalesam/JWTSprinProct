package com.olale.users.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.olale.users.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
