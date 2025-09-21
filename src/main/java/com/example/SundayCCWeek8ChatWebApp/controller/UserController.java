package com.example.SundayCCWeek8ChatWebApp.controller;

import com.example.SundayCCWeek8ChatWebApp.model.User;
import com.example.SundayCCWeek8ChatWebApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpServletRequest request, Model model) {
        User user = userRepository.findByName(username);

        if (user == null) {
            User newUser = new User();
            newUser.setName(username);
            newUser.setPassword(password);
            userRepository.save(newUser);

            request.getSession().setAttribute("user", newUser.getName());
            return "redirect:/home";
        }

        if (user.getPassword().equals(password)) {
            request.getSession().setAttribute("user", user.getName());
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid password.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
