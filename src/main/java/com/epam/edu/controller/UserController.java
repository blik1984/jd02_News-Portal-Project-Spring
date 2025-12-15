package com.epam.edu.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.epam.edu.entity.RegistrationInfo;
import com.epam.edu.entity.User;
import com.epam.edu.service.ServiceException;
import com.epam.edu.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping("/registration")
	public String doRegistration(@RequestParam String name, @RequestParam String email, @RequestParam String password,
			@RequestParam String passwordConfirm, RedirectAttributes redirectAttributes) {

		log.info("Попытка регистрации: email={}", email);

		if (!password.equals(passwordConfirm)) {
			log.warn("Пароли не совпадают для email={}", email);
			redirectAttributes.addAttribute("errorMessage", "Пароли не совпадают");
			return "redirect:/page_registration";
		}

		if (userService.existsByEmail(email)) {
			log.warn("Пользователь с таким email уже существует: email={}", email);
			redirectAttributes.addAttribute("errorMessage", "Пользователь с таким email уже существует");
			return "redirect:/page_registration";
		}

		RegistrationInfo info = new RegistrationInfo(email, password, name);
		if (!userService.addNew(info)) {
			log.error("Ошибка при создании пользователя: email={}", email);
			redirectAttributes.addAttribute("errorMessage", "Что-то пошло не так. Начните сначала.");
			return "redirect:/page_registration";
		}

		log.info("Регистрация успешна: email={}", email);
		return "redirect:/page_auth";
	}

	@PostMapping("/update_profile")
	public String saveInfo(@RequestParam String name, @RequestParam String surname,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirthday,
			Authentication auth, RedirectAttributes redirectAttributes) {

		String email = auth.getName();
		log.info("Обновление профиля пользователя: email={}", email);

		User user = userService.findByEmail(email);

		if (user != null) {
			user.setName(name);
			user.setSurname(surname);
			user.setDateOfBirthday(dateOfBirthday);
			userService.save(user);
			log.info("Профиль успешно обновлён: email={}", email);
			redirectAttributes.addFlashAttribute("successMessage", "Данные успешно сохранены");
		} else {
			log.warn("Профиль не найден для обновления: email={}", email);
			redirectAttributes.addFlashAttribute("errorMessage",
					"Не удалось сохранить данные. Пользователь не найден.");
		}
		return "redirect:/page_profile";
	}

	@PostMapping("/update_admin_user/{id}")
	public String updateAdminUser(@PathVariable Long id, @RequestParam(name = "active", required = false) Boolean activ,
			@RequestParam(name = "author", required = false) Boolean author, Authentication auth,
			RedirectAttributes redirectAttributes) {

		if (activ == null)
			activ = false;
		if (author == null)
			author = false;

		User currentAdmin = userService.findByEmail(auth.getName());
		User user = userService.findById(id);

		log.info("Админ пытается обновить пользователя: adminId={}, targetUserId={}, activ={}, author={}",
				currentAdmin != null ? currentAdmin.getId() : null, id, activ, author);

		if (user == null) {
			log.warn("Пользователь не найден: id={}", id);
			redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
			return "redirect:/page_admin_panel";
		}

		if (currentAdmin != null && user.getId().equals(currentAdmin.getId()) && !activ) {
			log.warn("Попытка самоблокировки админом: id={}", id);
			redirectAttributes.addFlashAttribute("errorMessage", "Нельзя деактивировать собственный аккаунт");
			return "redirect:/page_admin_panel";
		}

		user.setActiv(activ);
		user.setAuthor(author);
		userService.save(user);

		log.info("Пользователь успешно обновлён: id={}", id);
		redirectAttributes.addFlashAttribute("successMessage", "Данные пользователя успешно обновлены");

		return "redirect:/admin_panel";
	}

	@PostMapping("/delete")
	public String deleteUser(@RequestParam Long userId, Authentication auth, RedirectAttributes redirectAttributes) {

		User currentAdmin = userService.findByEmail(auth.getName());

		log.info("Запрос на удаление пользователя: adminId={}, targetUserId={}",
				currentAdmin != null ? currentAdmin.getId() : null, userId);

		try {
			userService.deleteUserByAdmin(userId, currentAdmin);
			redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удалён");
		} catch (ServiceException ex) {
			log.warn("Ошибка удаления пользователя: {}", ex.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/page_admin_panel";
	}

}
