package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    public void setUp() {
        testItem1 = new Item();
        testItem1.setId(1L);
        testItem1.setName("Test Item 1");
        testItem1.setPrice(new BigDecimal("10.0"));

        testItem2 = new Item();
        testItem2.setId(2L);
        testItem2.setName("Test Item 2");
        testItem2.setPrice(new BigDecimal("20.0"));
    }

    @Test
    public void testGetItems_Success() {
        List<Item> items = Arrays.asList(testItem1, testItem2);
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testGetItemById_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem1));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testItem1, response.getBody());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemById_NotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetItemsByName_Success() {
        List<Item> items = Arrays.asList(testItem1);
        when(itemRepository.findByName("Test Item 1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Test Item 1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(itemRepository, times(1)).findByName("Test Item 1");
    }

    @Test
    public void testGetItemsByName_NotFound() {
        when(itemRepository.findByName("Nonexistent")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemRepository, times(1)).findByName("Nonexistent");
    }

    @Test
    public void testGetItemsByName_EmptyList() {
        when(itemRepository.findByName("Empty")).thenReturn(Arrays.asList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Empty");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemRepository, times(1)).findByName("Empty");
    }
}
