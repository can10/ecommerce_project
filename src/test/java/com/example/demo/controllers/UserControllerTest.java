package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

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
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void verify_createUser() throws Exception {

        // create the user
        String username = "Adam";
        String password = "testPassword";
        String confirmPassword = "testPassword";

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(confirmPassword);

        // test creating the user
        when(encoder.encode(password)).thenReturn("thisIsHashed"); // for createUser(r)
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(username, u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void verify_findByUserName() {

        // create the user
        String username = "Adam";
        String password = "testPassword";
        String confirmPassword = "testPassword";

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(confirmPassword);

        when(encoder.encode(password)).thenReturn("thisIsHashed"); // for createUser(r)
        final ResponseEntity<User> responseForCreate = userController.createUser(r);
        User user = responseForCreate.getBody();

        // test finding by username
        when(userRepo.findByUsername(username)).thenReturn(user);
        final ResponseEntity<User> responseForFind = userController.findByUserName(username);

        assertNotNull(responseForFind);
        assertEquals(200, responseForFind.getStatusCodeValue());
        User u = responseForFind.getBody();
        assertNotNull(u);
        assertEquals(username, u.getUsername());
    }

    @Test
    public void verify_findByUserName_whenDoesNotExist() {
        // NoUser does not exist
        String username = "NoUser";

        final ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_findById() {

        // create the user
        String username = "Adam";
        String password = "testPassword";
        String confirmPassword = "testPassword";

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(confirmPassword);

        when(encoder.encode(password)).thenReturn("thisIsHashed"); // for createUser(r)
        final ResponseEntity<User> responseForCreate = userController.createUser(r);
        User user = responseForCreate.getBody();

        // test finding by id
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.ofNullable(user));
        final ResponseEntity<User> responseForFind = userController.findById(0L);

        assertNotNull(responseForFind);
        assertEquals(200, responseForFind.getStatusCodeValue());
        User u = responseForFind.getBody();
        assertEquals(0, u.getId());
        assertEquals(username, u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }
}
