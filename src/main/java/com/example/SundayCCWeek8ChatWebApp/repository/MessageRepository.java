package com.example.SundayCCWeek8ChatWebApp.repository;

import com.example.SundayCCWeek8ChatWebApp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}