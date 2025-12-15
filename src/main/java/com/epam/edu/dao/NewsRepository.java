package com.epam.edu.dao;

import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;

import java.util.List;

public interface NewsRepository extends BaseRepository<News, Long> {

	List<News> findFilteredNews(Integer groupId, Long authorId, boolean activ, int page, int size);

	long countFilteredNews(Integer groupId, Long authorId, boolean publishedOnly);

	List<NewsGroup> findAllNewsGroups();

	NewsGroup findNewsGroupById(Long newsGroupId);

}