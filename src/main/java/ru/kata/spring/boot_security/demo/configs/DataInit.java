package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInit implements CommandLineRunner {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInit(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userDao.listUsers().isEmpty()) {
            System.out.println("=== CREATING INITIAL DATA ===");


            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");

            userDao.addRole(roleUser);
            userDao.addRole(roleAdmin);

            Role savedRoleUser = userDao.findRoleByName("ROLE_USER");
            Role savedRoleAdmin = userDao.findRoleByName("ROLE_ADMIN");


            Set<Role> userRoles = new HashSet<>();
            userRoles.add(savedRoleUser);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setName("Иван");
            user.setAge(25);
            user.setGender("Мужской");
            user.setWork("Программист");
            user.setRoles(userRoles);

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(savedRoleUser);
            adminRoles.add(savedRoleAdmin);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Анна");
            admin.setAge(30);
            admin.setGender("Женский");
            admin.setWork("Менеджер");
            admin.setRoles(adminRoles);

            userDao.addUser(user);
            userDao.addUser(admin);

            System.out.println("=== INITIAL USERS CREATED ===");
            System.out.println("Username: user, Password: user, Roles: USER");
            System.out.println("Username: admin, Password: admin, Roles: ADMIN, USER");
        } else {
            System.out.println("=== DATA ALREADY EXISTS ===");
        }
    }
}