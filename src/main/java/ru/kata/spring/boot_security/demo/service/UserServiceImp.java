package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImp(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        logger.info("UserServiceImp: Список всех users");
        return userDao.listUsers();
    }

    @Override
    @Transactional
    public void addUserService(User user) {
        logger.info("UserServiceImp: Сохранение  user: {}", user.getUsername());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            logger.debug("UserServiceImp: Пароль, закодированный для user: {}", user.getUsername());
        }
        userDao.addUser(user);
        logger.info("UserServiceImp: User успешно сохранен: {}", user.getUsername());
    }

    @Override
    public User getUserById(Integer id) {
        logger.info("UserServiceImp: Получение user по id: {}", id);
        return userDao.getUserById(id);
    }


    @Override
    @Transactional
    public void deleteUserById(int id) {
        logger.info("UserServiceImp: Удадаление user по id: {}", id);
        userDao.deleteUserById(id);
        logger.info("UserServiceImp: User удален: id={}", id);
    }

    @Override
    public User findByUsername(String username) {
        logger.debug("UserServiceImp: Поиск user по username: {}", username);
        return userDao.findByUsername(username);
    }

    @Override
    public Set<Role> getAllRoles() {
        logger.debug("UserServiceImp: Получение всех roles");
        return userDao.getAllRoles();
    }

    @Override
    @Transactional
    public void saveUserWithRoles(User user, Set<Integer> roleIds) {
        logger.info("UserServiceImp: Сохранение user с roles: username={}, roleIds={}",
                user.getUsername(), roleIds);
        Set<Role> roles = new HashSet<>();
        for (Integer roleId : roleIds) {
            Role role = userDao.findRoleById(roleId);
            if (role != null) {
                roles.add(role);
                logger.debug("UserServiceImp: Добавлена role для user: role={}", role.getName());
            } else {
                logger.warn("UserServiceImp: Role с id {} не найдена", roleId);
            }
        }
        user.setRoles(roles);
        addUserService(user);
        logger.info("UserServiceImp: User с успешно сохраненными  roles: username={}, roles count={}",
                user.getUsername(), roles.size());

    }

    @Override
    @Transactional
    public void updateUserWithRoles(User user, Set<Integer> roleIds) {
        logger.info("UserServiceImp: Обновление user с roles: id={}, username={}",
                user.getId(), user.getUsername());
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
                logger.debug("UserServiceImp: Пароль у user: {} не изменён.", user.getUsername());
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.debug("UserServiceImp: Пароль обновлен для user: {}", user.getUsername());
        }

        userDao.addUser(user);
        logger.info("UserServiceImp: Service: User  успешно обновился: id={}", user.getId());
    }
}

