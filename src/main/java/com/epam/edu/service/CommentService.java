package com.epam.edu.service;

import java.util.List;
import java.util.Optional;

import com.epam.edu.entity.Comment;

public interface CommentService {
	
	void save(Comment comment);
	
	void delete(Long id);
	
	Optional<Comment> findById(Long id);
	
	List<Comment> findAllByNewsId(Long id);

}
