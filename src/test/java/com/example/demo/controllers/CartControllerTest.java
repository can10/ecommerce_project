package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
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

    // Helper method to create a user
    public User createUser(Long id, String username, String password, Cart cart) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(cart);
        return user;
    }

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void verify_addToCart() {

        // create cart, user and item
        Cart cart = new Cart();
        User user = createUser(0L, "Adam", "testPassword", cart);
        Item item1 = createItem(1L, "Round Widget 1", BigDecimal.valueOf(2.99), "Round widget type 1");

        // test adding the item to cart
        when(userRepo.findByUsername("Adam")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.of(item1));

        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1L);
        r.setQuantity(1);
        r.setUsername("Adam");

        ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cartCreated = response.getBody();
        assertNotNull(cartCreated);
        assertEquals(BigDecimal.valueOf(2.99), cartCreated.getTotal());
    }

    @Test
    public void verify_removeFromcart() {

        // create cart, user and item
        Cart cart = new Cart();
        User user = createUser(0L, "Adam", "testPassword", cart);
        Item item1 = createItem(1L, "Round Widget 3", BigDecimal.valueOf(2.50), "Round widget type 3");

        // test adding the item to cart
        when(userRepo.findByUsername("Adam")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.of(item1));

        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1L);
        r.setQuantity(3);     // adding three quantity to cart
        r.setUsername("Adam");

        ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // test removing one item from cart
        r = new ModifyCartRequest();
        r.setItemId(1L);
        r.setQuantity(1);  // remove one quantity
        r.setUsername("Adam");

        response = cartController.removeFromcart(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cartCreated = response.getBody();
        assertNotNull(cartCreated);
        assertEquals(BigDecimal.valueOf(5.0), cartCreated.getTotal());
    }



}
