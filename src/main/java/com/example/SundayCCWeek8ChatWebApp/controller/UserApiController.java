package com.example.SundayCCWeek8ChatWebApp.controllers;

import com.example.SundayCCWeek8ChatWebApp.model.User;
import com.example.SundayCCWeek8ChatWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/users/search")
    public List<String> searchUsers(@RequestParam("query") String query) {
        if (query.isEmpty()) {
            return List.of();
        }
        List<User> users = userRepository.findByNameContainingIgnoreCase(query);
        // only need the names
        return users.stream().map(User::getName).collect(Collectors.toList());
    }
}