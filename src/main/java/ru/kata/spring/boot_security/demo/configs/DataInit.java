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
import java.util.List;
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
        System.out.println("=== CHECKING INITIAL DATA ===");


        List<Role> existingRoles = userDao.listRoles();


        Role roleUser = userDao.findRoleByName("ROLE_USER");
        Role roleAdmin = userDao.findRoleByName("ROLE_ADMIN");

        if (roleUser == null) {
            System.out.println("Creating ROLE_USER...");
            roleUser = new Role("ROLE_USER");
            userDao.addRole(roleUser);
        } else {
            System.out.println("ROLE_USER already exists");
        }

        if (roleAdmin == null) {
            System.out.println("Creating ROLE_ADMIN...");
            roleAdmin = new Role("ROLE_ADMIN");
            userDao.addRole(roleAdmin);
        } else {
            System.out.println("ROLE_ADMIN already exists");
        }


        Role savedRoleUser = userDao.findRoleByName("ROLE_USER");
        Role savedRoleAdmin = userDao.findRoleByName("ROLE_ADMIN");


        List<User> existingUsers = userDao.listUsers();


        if (userDao.findByUsername("user") == null) {
            System.out.println("Creating user 'user'...");
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

            userDao.addUser(user);
            System.out.println("User 'user' created");
        } else {
            System.out.println("User 'user' already exists");
        }


        if (userDao.findByUsername("admin") == null) {
            System.out.println("Creating user 'admin'...");
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

            userDao.addUser(admin);
            System.out.println("User 'admin' created");
        } else {
            System.out.println("User 'admin' already exists");
        }

        System.out.println("=== INITIAL DATA CHECK COMPLETE ===");
    }
}