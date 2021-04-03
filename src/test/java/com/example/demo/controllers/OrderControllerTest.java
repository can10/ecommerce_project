package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

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
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }


    @Test
    public void verify_submit() {

        // create item, user and cart
        Item item1 = createItem(1L, "Round Widget 1", BigDecimal.valueOf(2.99), "Round widget type 1");
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);

        Cart cart = new Cart();
        User user = createUser(0L, "Adam", "testPassword", cart);
        cart.setId(0L);
        cart.setItems(itemList);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("2.99"));
        user.setCart(cart);

        // test submitting the order
        when(userRepo.findByUsername("Adam")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("Adam");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(2.99), order.getTotal());
        assertEquals("Adam", order.getUser().getUsername());
    }

    @Test
    public void verify_getOrdersForUser() {

        // create item, user and cart
        Item item1 = createItem(1L, "Round Widget 1", BigDecimal.valueOf(2.99), "Round widget type 1");
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);

        Cart cart = new Cart();
        User user = createUser(0L, "Adam", "testPassword", cart);
        cart.setId(0L);
        cart.setItems(itemList);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("2.99"));
        user.setCart(cart);

        // test getting orders for user
        when(userRepo.findByUsername("Adam")).thenReturn(user);

        orderController.submit("Adam"); // submit the order

        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("Adam");
        assertNotNull(ordersForUser);
        assertEquals(200, ordersForUser.getStatusCodeValue());
        List<UserOrder> orders = ordersForUser.getBody();
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }
}
