package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserDao {
    void addUser(User user);

    List<User> listUsers();

    User getUserById(int id);

    void deleteUserById(int id);

    User findByUsername(String username);


    void addRole(Role role);

    List<Role> listRoles();

    Role findRoleById(Integer id);

    Role findRoleByName(String name);

    Set<Role> getAllRoles();
}
