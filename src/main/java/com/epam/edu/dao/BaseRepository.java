package com.epam.edu.dao;

import java.util.List;
import java.util.Optional;

public interface BaseRepository <T, ID>{

	T save(T entity);
	
	Optional<T> findById(ID id);
	
	List<T> findAll();
	
	void delete (T entity);
	
	boolean deleteById(ID id);
	
	T update(T entity);
	
	void flush();
	
	void clear();
}
