package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPage(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        return "edit-user";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "selectedRoles", required = false) Set<Integer> roleIds) {
        if (roleIds == null) {
            roleIds = new HashSet<>();
        }
        userService.saveUserWithRoles(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Integer id, @RequestParam("username") String username, @RequestParam(value = "password", required = false) String password,
                             @RequestParam("name") String name, @RequestParam("age") Integer age, @RequestParam("gender") String gender,
                             @RequestParam("work") String work, @RequestParam(value = "selectedRoles", required = false) Set<Integer> roleIds) {

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setAge(age);
        user.setGender(gender);
        user.setWork(work);

        if (roleIds == null) {
            roleIds = new HashSet<>();
        }

        userService.updateUserWithRoles(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id.intValue());
        return "redirect:/admin";
    }
}
