package com.epam.edu.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.edu.dao.UserRepository;
import com.epam.edu.entity.RegistrationInfo;
import com.epam.edu.entity.User;
import com.epam.edu.entity.UserRole;
import com.epam.edu.service.ServiceException;
import com.epam.edu.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public User findByEmail(String email) {
		return repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Attempting to load user by email: {}", username);

		User user = repository.findByEmail(username).orElseThrow(() -> {
			log.warn("User not found with email: {}", username);
			return new UsernameNotFoundException("User not found with email: " + username);
		});

		log.info("Found user: id={}, email={}, roleId={}, activ={}", user.getId(), user.getEmail(), user.getRoleId(),
				user.isActiv());

		UserRole role = UserRole.fromId(user.getRoleId());

		log.info("Creating UserDetails: username={}, role={}, disabled={}", user.getEmail(), role.name(),
				!user.isActiv());

		return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
				.password(user.getPassword()).roles(role.name()).disabled(!user.isActiv()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> checkCredentials(String login, String password) {
		log.info("Checking credentials for email: {}", login);
		Optional<User> userOpt = repository.findByEmail(login)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()));

		if (userOpt.isPresent()) {
			log.info("Credentials are correct for user: {}", login);
		} else {
			log.warn("Credentials are incorrect for user: {}", login);
		}

		return userOpt;
	}

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		log.info("Searching for user by ID: {}", id);
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public boolean addNew(RegistrationInfo info) {
		log.info("Registering new user: email={}", info.getEmail());

		if (repository.existsByEmail(info.getEmail())) {
			log.warn("Email {} is already in use", info.getEmail());
			throw new ServiceException("Email is already in use");
		}

		User user = new User();
		user.setEmail(info.getEmail());
		user.setName(info.getName());
		user.setPassword(passwordEncoder.encode(info.getPassword()));
		user.setRegistrationDate(LocalDate.now());
		user.setRoleId(UserRole.USER.getId());
		user.setActiv(true);
		user.setAuthor(false);

		repository.save(user);
		log.info("User successfully registered: email={}", info.getEmail());

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> findAllAuthors() {
		log.info("Retrieving all authors");
		return repository.findAllAuthors();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}

	@Override
	@Transactional
	public User save(User user) {
		return repository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional
	public void deleteUserByAdmin(Long userId, User admin) {

		if (admin == null) {
			throw new ServiceException("Пользователь не авторизован");
		}

		if (admin.getRole() != UserRole.ADMIN) {
			throw new ServiceException("Недостаточно прав");
		}

		User user = repository.findById(userId).orElseThrow(() -> new ServiceException("Пользователь не найден"));

		if (user.getId().equals(admin.getId())) {
			throw new ServiceException("Нельзя удалить самого себя");
		}

		if (Boolean.TRUE.equals(user.getAuthor())) {
			throw new ServiceException("Нельзя удалить автора новостей");
		}

		log.info("Удаление пользователя: adminId={}, userId={}", admin.getId(), userId);

		repository.delete(user); 
	}

}
