package com.epam.edu.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.edu.dao.impl.CommentRepositoryImpl;
import com.epam.edu.entity.Comment;
import com.epam.edu.service.CommentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	
	private final CommentRepositoryImpl repository;

	@Override
	@Transactional
	public void save(Comment comment) {
		repository.save(comment);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		repository.deleteById(id);

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Comment> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Comment> findAllByNewsId(Long id) {
		return repository.findAllByNewsId(id);
	}
}
