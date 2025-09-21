package com.example.SundayCCWeek8ChatWebApp.repository;

import com.example.SundayCCWeek8ChatWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
    List<User> findByNameContainingIgnoreCase(String name);
}
