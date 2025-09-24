package com.olale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.olale.users.entities.Role;
import com.olale.users.entities.User;
import com.olale.users.service.UserService;

@SpringBootApplication
public class UserMicroserviceApplication {

    // Constantes pour éviter les duplications (SonarLint S1192)
    private static final String ADMIN_USERNAME = "admin";
    private static final String USER_USERNAME_1 = "nadhem";
    private static final String USER_USERNAME_2 = "yassine";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final UserService userService;

    // Injection par constructeur (meilleure pratique que @Autowired)
    public UserMicroserviceApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserMicroserviceApplication.class, args);
    }

    // Initialisation avec CommandLineRunner (au lieu de @PostConstruct)
    // @SuppressWarnings("unused")
    // @Bean
    // CommandLineRunner initUsers() {
    //     return args -> {
    //         // Ajouter les rôles
    //         userService.addRole(new Role(null, ROLE_ADMIN));
    //         userService.addRole(new Role(null, ROLE_USER));
    //         // Ajouter les utilisateurs
    //         userService.saveUser(new User(null, ADMIN_USERNAME, "123", true, null));
    //         userService.saveUser(new User(null, USER_USERNAME_1, "123", true, null));
    //         userService.saveUser(new User(null, USER_USERNAME_2, "123", true, null));
    //         // Assigner les rôles
    //         userService.addRoleToUser(ADMIN_USERNAME, ROLE_ADMIN);
    //         userService.addRoleToUser(ADMIN_USERNAME, ROLE_USER);
    //         userService.addRoleToUser(USER_USERNAME_1, ROLE_USER);
    //         userService.addRoleToUser(USER_USERNAME_2, ROLE_USER);
    //     };
    // }
    //Bean pour le password encoder
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
