package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;


	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username);
			if(user == null) {
				logger.warn("Order request failed: User not found for username: {}", username);
				return ResponseEntity.notFound().build();
			}
			UserOrder order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);
			logger.info("Order request succeeded for username: {}", username);
			return ResponseEntity.ok(order);
		} catch (Exception e) {
			logger.error("Order request failed with exception for username: {}", username, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username);
			if(user == null) {
				logger.warn("Order history request failed: User not found for username: {}", username);
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(orderRepository.findByUser(user));
		} catch (Exception e) {
			logger.error("Order history request failed with exception for username: {}", username, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
