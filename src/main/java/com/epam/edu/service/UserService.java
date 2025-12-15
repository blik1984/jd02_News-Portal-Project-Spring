package com.epam.edu.service;

import java.util.List;
import java.util.Optional;

import com.epam.edu.entity.RegistrationInfo;
import com.epam.edu.entity.User;

public interface UserService {

	Optional<User> checkCredentials(String login, String password);

	User findById(Long id);

	boolean addNew(RegistrationInfo info);

	List<User> findAllAuthors();

	User findByEmail(String email);

	boolean existsByEmail(String email);

	User save(User user);

	List<User> findAll();

	void deleteUserByAdmin(Long userId, User admin);

}
