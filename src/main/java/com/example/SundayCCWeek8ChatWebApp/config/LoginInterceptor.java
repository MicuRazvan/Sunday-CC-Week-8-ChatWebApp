package com.example.SundayCCWeek8ChatWebApp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//component to take care of login session
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // if session exists and has a "user" attribute, let the request proceed
        if (session != null && session.getAttribute("user") != null) {
            return true;
        }

        // if not logged in, redirect to the login page
        response.sendRedirect("/login");
        return false;
    }
}