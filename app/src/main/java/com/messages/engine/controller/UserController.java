package com.messages.engine.controller;

import com.messages.engine.dto.UserRequest;
import com.messages.engine.dto.UserResponse;
import com.messages.engine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for handling CRUD operations on users.
 * <p>
 * Provides endpoints to create, retrieve, update, and delete user records.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    /** Service layer for user-related operations. */
    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userRequest the user request DTO containing the new user's details.
     * @return a ResponseEntity containing the created UserResponse and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        log.info("Creating user with username: {}", userRequest.getUserName());
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return a ResponseEntity containing the UserResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Retrieving user with id: {}", id);
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Retrieves all users.
     *
     * @return a ResponseEntity containing a list of all UserResponse DTOs.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Retrieving all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user.
     *
     * @param id the unique identifier of the user to update.
     * @param userRequest the user request DTO containing updated user details.
     * @return a ResponseEntity containing the updated UserResponse.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody UserRequest userRequest) {
        log.info("Updating user with id: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the unique identifier of the user to delete.
     * @return a ResponseEntity with HTTP status NO CONTENT.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
