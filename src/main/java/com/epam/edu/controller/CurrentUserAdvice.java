package com.epam.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.epam.edu.entity.User;
import com.epam.edu.service.UserService;

@ControllerAdvice
public class CurrentUserAdvice {

	@Autowired
	private UserService userService;

	@ModelAttribute
	public void addCurrentUser(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			model.addAttribute("currentUserEmail", authentication.getName());
			User user = userService.findByEmail(authentication.getName());
			if (user != null) {
				model.addAttribute("currentUser", user);
				model.addAttribute("currentUserName", user.getName());
			}
		}
		model.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
	}
}