package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private User testUser;
    private Cart testCart;
    private UserOrder testOrder;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setItems(new ArrayList<>());
        testUser.setCart(testCart);

        testOrder = new UserOrder();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
    }

    @Test
    public void testSubmitOrder_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(testOrder);

        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_Success() {
        List<UserOrder> orders = Arrays.asList(testOrder);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(orderRepository.findByUser(testUser)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(orderRepository, times(1)).findByUser(testUser);
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
