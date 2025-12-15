package com.epam.edu.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epam.edu.entity.Comment;
import com.epam.edu.entity.News;
import com.epam.edu.entity.User;
import com.epam.edu.entity.UserRole;
import com.epam.edu.service.CommentService;
import com.epam.edu.service.NewsService;
import com.epam.edu.service.UserService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class CommentController {

	private final NewsService newsService;
	private final CommentService commentService;
	private final UserService userService;


	@PostMapping("/add_comment")
	public String addComment(@RequestParam Long newsId, @RequestParam String commentText,
			@RequestParam(required = false) Integer newsGroupId, @RequestParam(defaultValue = "0") int currentPage,
			Authentication auth) {

		User user = userService.findByEmail(auth.getName());
		News news = newsService.getNewsById(newsId);

		if (user != null && news != null) {
			Comment comment = new Comment();
			comment.setNews(news);
			comment.setUser(user);
			comment.setText(commentText);
			comment.setActiv(true);
			comment.setCreatedAt(LocalDateTime.now());
			commentService.save(comment);
		}

		return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d", newsId,
				newsGroupId != null ? newsGroupId : "", currentPage);
	}

	@PostMapping("/update_comment")
	public String updateComment(@RequestParam Long commentId, @RequestParam String commentText,
			@RequestParam Long newsId, @RequestParam(required = false) Integer newsGroupId,
			@RequestParam(defaultValue = "0") int currentPage, Authentication auth) {

		User user = userService.findByEmail(auth.getName());
		Comment comment = commentService.findById(commentId).orElse(null);

		if (comment != null && user != null && (user.getRole() == UserRole.ADMIN || comment.isEditable(user))) {
			comment.setText(commentText);
			commentService.save(comment);
		}

		return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d", newsId,
				newsGroupId != null ? newsGroupId : "", currentPage);
	}

	@PostMapping("/toggle_comment")
	public String toggleComment(@RequestParam Long commentId, @RequestParam Long newsId,
			@RequestParam(required = false) Integer newsGroupId, @RequestParam(defaultValue = "0") int page,
			Authentication auth) {

		User user = userService.findByEmail(auth.getName());
		if (user == null || user.getRole() != UserRole.ADMIN) {
			return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d&message=No access", newsId,
					newsGroupId != null ? newsGroupId : "", page);
		}

		Comment comment = commentService.findById(commentId).orElse(null);
		if (comment != null) {
			comment.setActiv(!comment.isActiv());
			commentService.save(comment);
		}

		return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d", newsId,
				newsGroupId != null ? newsGroupId : "", page);
	}

	@PostMapping("/delete_comment")
	public String deleteComment(@RequestParam Long commentId, @RequestParam Long newsId,
			@RequestParam(required = false) Integer newsGroupId, @RequestParam(defaultValue = "0") int page,
			Authentication auth) {

		User user = userService.findByEmail(auth.getName());
		if (user == null || user.getRole() != UserRole.ADMIN) {
			return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d&message=No access", newsId,
					newsGroupId != null ? newsGroupId : "", page);
		}

		commentService.delete(commentId);

		return String.format("redirect:/page_news?newsId=%d&newsGroupId=%s&page=%d", newsId,
				newsGroupId != null ? newsGroupId : "", page);
	}
}
