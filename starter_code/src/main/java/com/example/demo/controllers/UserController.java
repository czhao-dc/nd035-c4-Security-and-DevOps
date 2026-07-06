package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			String password = createUserRequest.getPassword();
			String confirmPassword = createUserRequest.getConfirmPassword();

			if (password == null || password.length() < 8) {
				logger.warn("CreateUser request failed: Password too short or null for username: {}", createUserRequest.getUsername());
				return ResponseEntity.badRequest().build();
			}

			if (!password.equals(confirmPassword)) {
				logger.warn("CreateUser request failed: Passwords do not match for username: {}", createUserRequest.getUsername());
				return ResponseEntity.badRequest().build();
			}

			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(password));
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			logger.info("CreateUser request succeeded for username: {}", createUserRequest.getUsername());
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			logger.error("CreateUser request failed with exception for username: {}", createUserRequest.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
