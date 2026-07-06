package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private Cart testCart;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");

        testCart = new Cart();
        testCart.setId(1L);
    }

    @Test
    public void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByUserName_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        ResponseEntity<User> response = userController.findByUserName("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    public void testFindByUserName_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    public void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("hashedpassword");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode("password123");
    }

    @Test
    public void testCreateUser_PasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("short");
        request.setConfirmPassword("short");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordNull() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword(null);
        request.setConfirmPassword(null);

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("different");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
