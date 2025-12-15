package com.epam.edu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epam.edu.entity.Comment;
import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;
import com.epam.edu.entity.User;
import com.epam.edu.entity.UserRole;
import com.epam.edu.service.CommentService;
import com.epam.edu.service.NewsService;
import com.epam.edu.service.PagedResult;
import com.epam.edu.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

	private static final Logger logger = LoggerFactory.getLogger(PageController.class);

	private final NewsService newsService;
	private final CommentService commentService;
	private final UserService userService;

	@GetMapping("/")
	public String pageMain(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size,
			@RequestParam(required = false) Integer newsGroupId, Authentication authentication, Model model) {
		logger.info("Главная страница. page={}, size={}, newsGroupId={}", page, size, newsGroupId);
		UserRole userRole = determineUserRole(authentication);
		logger.debug("Определена роль пользователя: {}", userRole);

		PagedResult<News> result = newsService.getNewsForPage(page, size, userRole, newsGroupId);
		model.addAttribute("newsList", result.getContent());
		model.addAttribute("currentPage", result.getCurrentPage());
		model.addAttribute("pageSize", result.getPageSize());
		model.addAttribute("totalPages", result.getTotalPages());
		model.addAttribute("newsGroupId", newsGroupId);

		return "main";
	}

	@GetMapping("/page_news")
	public String pageNews(@RequestParam(required = false) Long newsId,
			@RequestParam(required = false) Integer newsGroupId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) Long editingCommentId, Authentication authentication, Model model) {

		logger.info("Просмотр новости. newsId={}, newsGroupId={}, page={}, editingCommentId={}", newsId, newsGroupId,
				page, editingCommentId);
		UserRole userRole = determineUserRole(authentication);
		logger.debug("Определена роль пользователя: {}", userRole);

		News news = null;

		try {
			if (newsId != null) {
				news = newsService.getNewsById(newsId);
				if (news != null && userRole != UserRole.ADMIN) {
					boolean isActive = news.isActiv();
					boolean isPublished = news.getPublishingDateTime() == null
							|| news.getPublishingDateTime().isBefore(java.time.LocalDateTime.now());
					if (!isActive || !isPublished) {
						logger.debug("Новость не активна или еще не опубликована: newsId={}", newsId);
						news = null;
					}
				}
			}

			if (news == null) {
				logger.warn("Новость не найдена или недоступна: newsId={}", newsId);
				news = new News();
				news.setTitle("Sorry, this news has not been created yet.");
				news.setBrief("Sorry, this news has not been created yet.");
				news.setContent("Sorry, this news has not been created yet.");
			}

			List<Comment> allComments = commentService.findAllByNewsId(newsId);
			User currentUser = (User) model.getAttribute("currentUser");
			boolean isAdmin = currentUser != null && currentUser.getRole() == UserRole.ADMIN;

			List<Comment> comments = allComments.stream().filter(c -> c.isActiv() || isAdmin)
					.peek(c -> c.setEditable(c.isEditable(currentUser))).toList();

			model.addAttribute("news", news);
			model.addAttribute("userRole", userRole);
			model.addAttribute("comments", comments);
			model.addAttribute("editingCommentId", editingCommentId);
			model.addAttribute("newsGroupId", newsGroupId);
			model.addAttribute("currentPage", page);
		} catch (Exception e) {
			logger.error("Ошибка при формировании страницы новости. newsId={}", newsId, e);
		}

		return "news";
	}

	@GetMapping("/page_edit_news")
	public String pageEditNews(@RequestParam Long newsId, @RequestParam(required = false) Integer newsGroupId,
			@RequestParam(defaultValue = "0") int currentPage, Authentication auth, Model model) {
		logger.info("Редактирование новости. newsId={}, newsGroupId={}, currentPage={}", newsId, newsGroupId,
				currentPage);

		News news = newsService.getNewsById(newsId);
		if (news == null) {
			logger.warn("Новость не найдена: newsId={}", newsId);
			return "error";
		}

		List<User> authors = userService.findAllAuthors();
		List<NewsGroup> newsGroups = newsService.findAllNewsGroups();

		model.addAttribute("news", news);
		model.addAttribute("authors", authors);
		model.addAttribute("newsGroups", newsGroups);
		model.addAttribute("newsGroupId", newsGroupId);
		model.addAttribute("currentPage", currentPage);

		return "create_news";
	}

	@GetMapping("/page_create_news")
	public String pageCreateNews(Model model) {
		logger.info("Страница создания новости");
		List<User> authors = userService.findAllAuthors();
		List<NewsGroup> newsGroups = newsService.findAllNewsGroups();

		model.addAttribute("news", new News());
		model.addAttribute("authors", authors);
		model.addAttribute("newsGroups", newsGroups);

		return "create_news";
	}

	@GetMapping("/page_privacy")
	public String pagePrivacy() {
		logger.info("Страница политики конфиденциальности");
		return "privacy";
	}

	@GetMapping("/page_auth")
	public String loginPage() {
		logger.info("Страница авторизации");
		return "auth";
	}

	@GetMapping("/page_registration")
	public String registrationPage() {
		logger.info("Страница регистрации");
		return "registration";
	}

	@GetMapping("/error")
	public String error() {
		logger.error("Обращение к странице ошибки");
		return "error";
	}

	@GetMapping("/page_profile")
	public String pageProfile(Authentication auth, Model model) {
		String email = auth.getName();
		logger.info("Страница профиля пользователя: {}", email);
		User user = userService.findByEmail(email);
		if (user == null) {
			logger.warn("Пользователь не найден: {}", email);
		}
		model.addAttribute("user", user);

		return "profile";
	}

	@GetMapping("/admin_panel")
	public String pageAdminUsers(Model model, Authentication auth) {
		String email = auth.getName();
		User currentUser = userService.findByEmail(email);
		if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
			logger.warn("Несанкционированный доступ к админ-панели: {}", email);
			return "redirect:/";
		}
		logger.info("Админ-панель. Пользователь: {}", email);

		model.addAttribute("users", userService.findAll());
		model.addAttribute("user", currentUser);
		return "admin_panel";
	}

	private UserRole determineUserRole(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return UserRole.GUEST;
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			String roleName = authority.getAuthority();
			if (roleName.contains("ADMIN"))
				return UserRole.ADMIN;
			if (roleName.contains("USER"))
				return UserRole.USER;
		}
		return UserRole.GUEST;
	}
}
