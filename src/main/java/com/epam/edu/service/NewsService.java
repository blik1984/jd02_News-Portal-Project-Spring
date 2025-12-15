package com.epam.edu.service;

import java.util.List;

import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;
import com.epam.edu.entity.UserRole;

public interface NewsService {

	News saveNews(News news);

	News getNewsById(Long id);

	boolean deleteNews(Long id);

	PagedResult<News> getNewsForPage(int page, int size, UserRole userRole, Integer newsGroupId);

	List<NewsGroup> findAllNewsGroups();

	NewsGroup findNewsGroupById(Long newsGroupId);
}
