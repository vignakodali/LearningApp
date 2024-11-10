package com.example.LearningPlatform.controller;

import com.example.LearningPlatform.service.CognitoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    private CognitoService cognitoService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public ModelAndView login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {

        String userRole = cognitoService.authenticateUser(username, password);

        if (userRole != null) {
            HttpSession session = request.getSession();
            session.setAttribute("studentId", username); 

            if ("instructor".equalsIgnoreCase(userRole)) {
                return new ModelAndView("redirect:/instructor/dashboard");
            } else if ("student".equalsIgnoreCase(userRole)) {
                return new ModelAndView("redirect:/student/dashboard");
            } else {
                return new ModelAndView("redirect:/error");
            }
        } else {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("error", "Invalid username or password. Please try again.");
            return modelAndView;
        }
    }
}
