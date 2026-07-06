package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ItemRepository itemRepository;

	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		try {
			User user = userRepository.findByUsername(request.getUsername());
			if(user == null) {
				logger.warn("AddToCart request failed: User not found for username: {}", request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if(!item.isPresent()) {
				logger.warn("AddToCart request failed: Item not found for itemId: {}", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
				.forEach(i -> cart.addItem(item.get()));
			cartRepository.save(cart);
			logger.info("AddToCart request succeeded for username: {}, itemId: {}, quantity: {}",
				request.getUsername(), request.getItemId(), request.getQuantity());
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			logger.error("AddToCart request failed with exception for username: {}", request.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		try {
			User user = userRepository.findByUsername(request.getUsername());
			if(user == null) {
				logger.warn("RemoveFromCart request failed: User not found for username: {}", request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if(!item.isPresent()) {
				logger.warn("RemoveFromCart request failed: Item not found for itemId: {}", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
				.forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);
			logger.info("RemoveFromCart request succeeded for username: {}, itemId: {}, quantity: {}",
				request.getUsername(), request.getItemId(), request.getQuantity());
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			logger.error("RemoveFromCart request failed with exception for username: {}", request.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
