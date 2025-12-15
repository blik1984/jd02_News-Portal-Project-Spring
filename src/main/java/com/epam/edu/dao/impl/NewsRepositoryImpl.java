package com.epam.edu.dao.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.epam.edu.dao.NewsRepository;
import com.epam.edu.entity.News;
import com.epam.edu.entity.NewsGroup;

@Repository
public class NewsRepositoryImpl extends NewsPortalBaseRepository<News, Long> implements NewsRepository {

	protected NewsRepositoryImpl() {
		super(News.class);
	}

	@Override
	public List<News> findFilteredNews(Integer groupId, Long authorId, boolean publishedOnly, int page, int size) {
		Session session = entityManager.unwrap(Session.class);

		String hql = "SELECT DISTINCT n FROM News n " + "LEFT JOIN n.authors a "
				+ "WHERE (:groupId IS NULL OR n.newsGroup.id = :groupId) "
				+ "AND (:authorId IS NULL OR a.id = :authorId) "
				+ "AND (:publishedOnly = FALSE OR (n.activ = TRUE AND n.publishingDateTime <= :currentTime)) "
				+ "ORDER BY n.publishingDateTime DESC NULLS LAST, n.id DESC";

		Query<News> query = session.createQuery(hql, News.class);
		query.setParameter("groupId", groupId);
		query.setParameter("authorId", authorId);
		query.setParameter("publishedOnly", publishedOnly);
		query.setParameter("currentTime", LocalDateTime.now());

		query.setFirstResult(page * size);
		query.setMaxResults(size);

		return query.getResultList();
	}

	@Override
	public long countFilteredNews(Integer groupId, Long authorId, boolean publishedOnly) {
		Session session = entityManager.unwrap(Session.class);

		String hql = "SELECT COUNT(DISTINCT n) FROM News n " + "LEFT JOIN n.authors a "
				+ "WHERE (:groupId IS NULL OR n.newsGroup.id = :groupId) "
				+ "AND (:authorId IS NULL OR a.id = :authorId) "
				+ "AND (:publishedOnly = FALSE OR (n.activ = TRUE AND n.publishingDateTime <= :currentTime))";

		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("groupId", groupId);
		query.setParameter("authorId", authorId);
		query.setParameter("publishedOnly", publishedOnly);
		query.setParameter("currentTime", LocalDateTime.now());

		return query.getSingleResult();
	}

	@Override
	public List<NewsGroup> findAllNewsGroups() {

		Session session = entityManager.unwrap(Session.class);

		String hql = "FROM NewsGroup ng ORDER BY ng.name";

		Query<NewsGroup> query = session.createQuery(hql, NewsGroup.class);

		return query.getResultList();
	}

	@Override
	public NewsGroup findNewsGroupById(Long newsGroupId) {
		if (newsGroupId == null) {
			return null;
		}
		String hql = "FROM NewsGroup ng WHERE ng.id = :id";
		return entityManager.createQuery(hql, NewsGroup.class).setParameter("id", newsGroupId).getSingleResult();
	}
	
}