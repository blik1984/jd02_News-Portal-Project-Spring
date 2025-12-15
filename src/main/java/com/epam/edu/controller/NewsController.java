package com.epam.edu.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;
import com.epam.edu.entity.User;
import com.epam.edu.service.NewsService;
import com.epam.edu.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class NewsController {

	private final NewsService newsService;
	private final UserService userService;

	@PostMapping("/save_news")
	public String saveNews(@ModelAttribute("news") News news, @RequestParam Long newsGroupId,
			@RequestParam(required = false) List<Long> authorIds, Authentication authentication) {

		NewsGroup group = newsService.findNewsGroupById(newsGroupId);
		news.setNewsGroup(group);

		if (authorIds != null && !authorIds.isEmpty()) {
			List<User> authors = authorIds.stream().map(userService::findById).collect(Collectors.toList());

			news.setAuthors(authors);
		} else {
			news.setAuthors(Collections.emptyList());
		}
		if (authentication != null && authentication.isAuthenticated()) {
			User currentUser = userService.findByEmail(authentication.getName());
			if (currentUser != null) {
				news.setPublisher(currentUser);
			}
		}

		newsService.saveNews(news);

		return "redirect:/";
	}

	@PostMapping("/delete_news")
	public String deleteNews(@RequestParam Long newsId, @RequestParam(required = false) Integer newsGroupId,
			@RequestParam(defaultValue = "0") int currentPage, Authentication auth) {
		newsService.deleteNews(newsId);

		return "redirect:/?page=" + currentPage + (newsGroupId != null ? "&newsGroupId=" + newsGroupId : "");
	}

}
