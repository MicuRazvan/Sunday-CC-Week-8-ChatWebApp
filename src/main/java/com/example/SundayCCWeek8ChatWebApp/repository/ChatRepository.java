package com.example.SundayCCWeek8ChatWebApp.repository;


import com.example.SundayCCWeek8ChatWebApp.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
