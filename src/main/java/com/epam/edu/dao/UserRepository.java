package com.epam.edu.dao;

import java.util.List;
import java.util.Optional;
import com.epam.edu.entity.User;

public interface UserRepository extends BaseRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findAllAuthors();

	Optional<User> findById(Long id);

}
