package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HelloController {

    private final UserService userService;

    @Autowired
    public HelloController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public String printWelcome(ModelMap model) {
        List<User> user = userService.listUsers();
        model.addAttribute("userGetList", user);
        return "allUsers";
    }


    @GetMapping(value = "/addNewUser")
    public String addNewUser(ModelMap model) {
        User user = new User();
        model.addAttribute("userAdd", user);
        return "user-add-new-user";
    }

    @PostMapping(value = "/saveUser")
    public String saveUser(@ModelAttribute("userAdd") User user) {
        userService.addUserService(user);
        return "redirect:/";
    }

    @GetMapping("/updateInfo")
    public String updateUserForm(@RequestParam("id") int id, ModelMap model) {
        User user = userService.getUserById(id);
        model.addAttribute("userAdd", user);
        return "user-add-new-user";
    }

    @GetMapping("/deleteUser")
    public String deleteUserById(@RequestParam("id") int id) {
        userService.deleteUserById(id);
        return "redirect:/";
    }
}