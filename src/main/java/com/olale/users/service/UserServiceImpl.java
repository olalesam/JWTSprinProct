package com.olale.users.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olale.users.entities.Role;
import com.olale.users.entities.User;
import com.olale.users.repo.RoleRepository;
import com.olale.users.repo.UserRepository;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRep;
    private final RoleRepository roleRep;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Injection par constructeur (recommandée par Spring)
    public UserServiceImpl(UserRepository userRep,
            RoleRepository roleRep,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRep = userRep;
        this.roleRep = roleRep;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRep.save(user);
    }

    @Override
    public User addRoleToUser(String username, String rolename) {
        User usr = userRep.findByUsername(username);
        Role r = roleRep.findByRoleName(rolename);

        if (usr == null || r == null) {
            throw new IllegalArgumentException("User or Role not found");
        }

        usr.getRoles().add(r);
        return userRep.save(usr);
    }

    @Override
    public Role addRole(Role role) {
        return roleRep.save(role);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRep.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRep.findAll();
    }
}
