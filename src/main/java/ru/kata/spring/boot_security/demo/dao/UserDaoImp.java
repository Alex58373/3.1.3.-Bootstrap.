package ru.kata.spring.boot_security.demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserDaoImp implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImp.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(User user) {
        logger.debug("Добавление нового пользователя: username={}", user.getUsername());

        try {
            if (user.getId() == null) {
                entityManager.persist(user);
                logger.info("Пользователь успешно сохранился: username={}, id={}", user.getUsername(), user.getId());
            } else {
                entityManager.merge(user);
                logger.info("Пользователь объединен/обновлен: username={}, id={}", user.getUsername(), user.getId());
            }
        } catch (Exception e) {
            logger.error("Ошибка при добавлении/обновлении пользователя: username={}, error={}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<User> listUsers() {
        logger.debug("Получение списка всех пользователей с ролями");

        try {
            List<User> users = entityManager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class)
                    .getResultList();
            logger.info("Получено {} пользователей с указанием их ролей", users.size());
            return users;

        } catch (Exception e) {
            logger.error("Ошибка при поиске списка пользователей: {}", e.getMessage(), e);
            throw e;

        }
    }

    @Override
    public User getUserById(int id) {
        logger.debug("Поиск пользователя по идентификатору: {}", id);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
            query.setParameter("id", id);
            User user = query.getSingleResult();

            logger.info("Пользователь найден: id={}, username={}, roles count={}",
                    user.getId(), user.getUsername(), user.getRoles().size());
            return user;

        } catch (NoResultException e) {
            logger.warn("Пользователь по id: {} не найден", id);
            throw new EntityNotFoundException("Пользователь не найден с id: " + id);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по  id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteUserById(int id) {
        logger.debug("Удаление пользователя по id: {}", id);
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                String username = user.getUsername();
                entityManager.remove(user);
                logger.info("Пользователь успешно удален: id={}, username={}", id, username);
            } else {
                logger.warn("Попытка удалить несуществующего пользователя с id: {}", id);
            }
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User findByUsername(String username) {
        logger.debug("Поиск пользователя по id: {}", username);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();

            logger.info("Пользователь с  username: username={}, id={}, roles count={}, найден.",
                    user.getUsername(), user.getId(), user.getRoles().size());
            return user;
        } catch (NoResultException e) {
            logger.debug("Пользователь с username: {} не найдена", username);
            return null;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по username {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void addRole(Role role) {
        logger.debug("Добавление role: {}", role.getName());
        try {
            if (role.getId() == null) {
                entityManager.persist(role);
                logger.info("Role сохранялась: name={}, id={}", role.getName(), role.getId());
            } else {
                entityManager.merge(role);
                logger.info("Role объединена/обновлена: name={}, id={}", role.getName(), role.getId());
            }
        } catch (Exception e) {
            logger.error("Ошибка при добавлении role {}: {}", role.getName(), e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public List<Role> listRoles() {
        logger.debug("Поиск всех ролей");
        try {
            List<Role> roles = entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
            logger.info("Извлеченные {} roles", roles.size());
            return roles;
        } catch (Exception e) {
            logger.error("Ошибка при поиске списка roles: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Role findRoleById(Integer id) {
        logger.debug("Выбор role по id: {}", id);
        try {
            Role role = entityManager.find(Role.class, id);
            if (role != null) {
                logger.debug("Role найдена: id={}, name={}", id, role.getName());
            } else {
                logger.debug("Role по id: {} не найдена.", id);
            }
            return role;
        } catch (Exception e) {
            logger.error("Ошибка при поиске role по id {}: {}.", id, e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Role findRoleByName(String name) {
        logger.debug("Поиск role по name: {}", name);
        try {
            TypedQuery<Role> query = entityManager.createQuery(
                    "SELECT r FROM Role r WHERE r.name = :name", Role.class);
            query.setParameter("name", name);
            Role role = query.getSingleResult();
            logger.debug("Role найдена: name={}, id={}", name, role.getId());
            return role;
        } catch (NoResultException e) {
            logger.debug("Role по name: {} не найдена", name);
            return null;
        } catch (Exception e) {
            logger.error("Ошибка поиска  role по name {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Set<Role> getAllRoles() {
        logger.debug("Получение всех roles в виде Set");
        try {
            List<Role> roles = listRoles();
            Set<Role> roleSet = roles.stream().collect(Collectors.toSet());
            logger.debug("Преобразование {} roles в Set", roleSet.size());
            return roleSet;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех roles в виде Set: {}", e.getMessage(), e);
            throw e;
        }

    }
}
