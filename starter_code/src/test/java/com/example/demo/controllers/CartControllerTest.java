package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    private User testUser;
    private Cart testCart;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCart = new Cart();
        testCart.setId(1L);
        testUser.setCart(testCart);

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setPrice(new BigDecimal("10.0"));
    }

    @Test
    public void testAddToCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(itemRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistent");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(999L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(itemRepository, times(1)).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(itemRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistent");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(999L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(itemRepository, times(1)).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
