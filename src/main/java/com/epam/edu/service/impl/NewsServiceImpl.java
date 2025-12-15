package com.epam.edu.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.edu.dao.NewsRepository;
import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;
import com.epam.edu.entity.UserRole;
import com.epam.edu.service.NewsService;
import com.epam.edu.service.PagedResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;

	@Override
	@Transactional
	public News saveNews(News news) {
		String filePath = saveContentToFile(news.getContent());
		news.setContentPath(filePath);
		LocalDateTime now = LocalDateTime.now();
		if (news.getId() == null) {
			news.setCreateDateTime(now);
		}
		news.setUpdateDateTime(now);
		return newsRepository.save(news);
	}

	@Override
	@Transactional(readOnly = true)
	public News getNewsById(Long id) {
		return newsRepository.findById(id).map(n -> {
			if (n.getContentPath() != null) {
				try {
					n.setContent(Files.readString(Path.of(n.getContentPath())));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return n;
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<News> getNewsForPage(int page, int size, UserRole userRole, Integer newsGroupId) {

		int pageSize = validatePageSize(size);
		boolean onlyPublished = (userRole != UserRole.ADMIN);
		long total = newsRepository.countFilteredNews(newsGroupId, null, onlyPublished);
		
		List<News> content = newsRepository.findFilteredNews(newsGroupId, null, onlyPublished, page, pageSize);

		return new PagedResult<>(content, page, pageSize, total);
	}

	@Transactional
	@Override
	public boolean deleteNews(Long id) {
		return newsRepository.deleteById(id);
	}

	private int validatePageSize(int size) {
		if (size <= 3)
			return 3;
		if (size <= 6)
			return 6;
		return 9;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsGroup> findAllNewsGroups() {
		return newsRepository.findAllNewsGroups();
	}

	@Override
	@Transactional(readOnly = true)
	public NewsGroup findNewsGroupById(Long newsGroupId) {
		return newsRepository.findNewsGroupById(newsGroupId);
	}

	private String saveContentToFile(String text) {
		try {
			Path dir = Path.of("resources/news/content");
			Files.createDirectories(dir);
			Path file = dir.resolve("news_" + System.currentTimeMillis() + ".txt");
			Files.writeString(file, text);
			return file.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}