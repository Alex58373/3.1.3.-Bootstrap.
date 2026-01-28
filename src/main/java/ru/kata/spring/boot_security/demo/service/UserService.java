package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> listUsers();

    void addUserService(User user);

    User getUserById(int id);

    void deleteUserById(int id);


    User findByUsername(String username);

    Set<Role> getAllRoles();

    void saveUserWithRoles(User user, Set<Integer> roleIds);

    void updateUserWithRoles(User user, Set<Integer> roleIds);

    User getUserById(Integer id);
}
