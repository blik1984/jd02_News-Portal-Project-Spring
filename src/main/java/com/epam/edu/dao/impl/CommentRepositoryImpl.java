package com.epam.edu.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.epam.edu.dao.CommentRepository;
import com.epam.edu.entity.Comment;

@Repository
public class CommentRepositoryImpl extends NewsPortalBaseRepository<Comment, Long> implements CommentRepository {

	protected CommentRepositoryImpl() {
		super(Comment.class);
	}

	@Override
	public void deleteAllByNewsId(Long newsId) {
		entityManager.createQuery("""
				    DELETE FROM Comment c
				    WHERE c.news.id = :newsId
				""").setParameter("newsId", newsId).executeUpdate();
	}

	@Override
	public List<Comment> findAllByNewsId(Long newsId) {
		return entityManager.createQuery("""
				    SELECT c FROM Comment c
				    WHERE c.news.id = :newsId
				    ORDER BY c.createdAt ASC
				""", Comment.class).setParameter("newsId", newsId).getResultList();
	}
}
