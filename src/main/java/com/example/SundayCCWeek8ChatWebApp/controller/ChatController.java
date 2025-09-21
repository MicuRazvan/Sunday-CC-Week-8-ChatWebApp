package com.example.SundayCCWeek8ChatWebApp.controller;

import com.example.SundayCCWeek8ChatWebApp.model.Chat;
import com.example.SundayCCWeek8ChatWebApp.model.Message;
import com.example.SundayCCWeek8ChatWebApp.model.User;
import com.example.SundayCCWeek8ChatWebApp.repository.ChatRepository;
import com.example.SundayCCWeek8ChatWebApp.repository.MessageRepository;
import com.example.SundayCCWeek8ChatWebApp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

import java.util.Collections;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("user");
        //if no user logged we redirect to login
        if (username == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByName(username);
        List<Chat> userChats = user != null ? user.getChats() : Collections.emptyList();

        model.addAttribute("username", username);
        model.addAttribute("userChats", userChats);
        return "home";
    }

    @PostMapping("/create-chat")
    public String createChat(@RequestParam String chatName, HttpSession session) {
        String username = (String) session.getAttribute("user");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByName(username);
        if (user != null && chatName != null && !chatName.trim().isEmpty()) {
            Chat newChat = new Chat();
            newChat.setName(chatName.trim());
            newChat.getUsers().add(user);

            user.getChats().add(newChat);
            chatRepository.save(newChat);
        }

        return "redirect:/home";
    }

    @PostMapping("/chat/{chatId}/addUser")
    @ResponseBody // tells Spring to return data, not a view name
    public ResponseEntity<Map<String, String>> addUserToChat(@PathVariable Long chatId, @RequestParam String username) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        User userToAdd = userRepository.findByName(username);

        if (userToAdd == null) {
            Map<String, String> error = Map.of("error", "User '" + username + "' not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
        }

        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();

            if (chat.getUsers().stream().anyMatch(user -> user.getId() == userToAdd.getId())) {
                Map<String, String> error = Map.of("error", "User '" + username + "' is already in this chat.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409 Conflict
            }

            chat.getUsers().add(userToAdd);
            userToAdd.getChats().add(chat);
            chatRepository.save(chat);

            Map<String, String> successResponse = Map.of("name", userToAdd.getName());
            return ResponseEntity.ok(successResponse);

        }

        Map<String, String> error = Map.of("error", "Chat not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @PostMapping("/chat/{chatId}/leave")
    public String leaveChat(@PathVariable Long chatId, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("user");
        if (username == null) {
            return "redirect:/login";
        }

        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        User currentUser = userRepository.findByName(username);

        if (chatOptional.isPresent() && currentUser != null) {
            Chat chat = chatOptional.get();

            boolean removed = chat.getUsers().removeIf(user -> user.getId() == currentUser.getId());

            if (removed) {
                currentUser.getChats().remove(chat);

                // if the chat is now empty, delete it
                if (chat.getUsers().isEmpty()) {
                    chatRepository.delete(chat);
                } else {
                    chatRepository.save(chat);
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error_global", "Could not leave chat. Chat or user not found.");
        }

        return "redirect:/home";
    }

    @PostMapping("/chat/{chatId}/sendMessage")
    @ResponseBody
    public ResponseEntity<Void> sendMessage(@PathVariable Long chatId, @RequestParam String content, HttpSession session) {
        String username = (String) session.getAttribute("user");
        if (username == null || content == null || content.trim().isEmpty()) {
            // bad request, return an error status
            return ResponseEntity.badRequest().build();
        }

        User currentUser = userRepository.findByName(username);
        Optional<Chat> chatOptional = chatRepository.findById(chatId);

        if (currentUser != null && chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            Message newMessage = new Message();
            newMessage.setContent(content.trim());
            newMessage.setUser(currentUser);
            newMessage.setChat(chat);
            messageRepository.save(newMessage);

            // success, return an OK status
            return ResponseEntity.ok().build();
        }

        // something went wrong (user/chat not found), return an error status
        return ResponseEntity.badRequest().build();
    }
}
