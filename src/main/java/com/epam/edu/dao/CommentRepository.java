package com.epam.edu.dao;

import java.util.List;

import com.epam.edu.entity.Comment;

public interface CommentRepository extends BaseRepository<Comment, Long> {

	void deleteAllByNewsId(Long newsId);
	
	List<Comment> findAllByNewsId(Long id);

}
