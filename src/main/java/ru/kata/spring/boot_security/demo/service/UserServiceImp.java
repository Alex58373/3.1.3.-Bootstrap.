package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImp(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    @Transactional
    public void addUserService(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userDao.addUser(user);
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public User getUserById(Integer id) {
        return userDao.getUserById(id.intValue());
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Set<Role> getAllRoles() {
        return userDao.getAllRoles();
    }

    @Override
    @Transactional
    public void saveUserWithRoles(User user, Set<Integer> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (Integer roleId : roleIds) {
            Role role = userDao.findRoleById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
        addUserService(user);
    }

    @Override
    @Transactional
    public void updateUserWithRoles(User user, Set<Integer> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (Integer roleId : roleIds) {
            Role role = userDao.findRoleById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        user.setRoles(roles);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            User existingUser = getUserById(user.getId());
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userDao.addUser(user);
    }
}

