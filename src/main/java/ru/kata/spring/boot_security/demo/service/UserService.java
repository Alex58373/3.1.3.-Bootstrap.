package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> listUsers();

    void addUserService(User user);

    User getUserById(int id);

    void deleteUserById(int id);
}
