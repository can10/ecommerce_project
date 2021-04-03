package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    // Helper method to create an item
    public Item createItem(Long id, String name, BigDecimal price, String description) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void verify_getItems() {

        // create item list
        Item item1 = createItem(1L, "Round Widget 1", BigDecimal.valueOf(2.99), "Round widget type 1");
        Item item2 = createItem(1L, "Round Widget 2", BigDecimal.valueOf(3.99), "Round widget type 2");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // test getting all items
        when(itemRepo.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemsRetrieved = response.getBody();
        assertNotNull(itemsRetrieved);
        assertEquals(2, items.size());
    }

    @Test
    public void verify_getItemById() {

        // create single item with id 5
        Item item1 = createItem(5L, "Round Widget 1", BigDecimal.valueOf(2.99), "Round widget type 1");

        // test getting the item by id
        when(itemRepo.findById(5L)).thenReturn(java.util.Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(5L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item itemRetrieved = response.getBody();
        assertNotNull(itemRetrieved);
        assertEquals(item1, itemRetrieved);
    }

    @Test
    public void verify_getItemsByName() {

        // create item list
        String itemName = "Round Widget 1";
        Item item1 = createItem(1L, itemName, BigDecimal.valueOf(2.99), "Round widget type 1");
        List<Item> items = new ArrayList<>();
        items.add(item1);

        // test getting the item by name
        when(itemRepo.findByName(itemName)).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemsRetrieved = response.getBody();
        assertNotNull(itemsRetrieved);
        assertEquals(item1, itemsRetrieved.get(0));
    }

}
