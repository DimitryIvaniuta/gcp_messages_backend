package com.messages.engine.service;

import com.messages.engine.dto.UserRequest;
import com.messages.engine.dto.UserResponse;

import java.util.List;

/**
 * Service interface for managing user operations.
 */
public interface UserService {

    /**
     * Creates a new user.
     *
     * @param userRequest the DTO containing the new user's information.
     * @return the created UserResponse DTO.
     */
    UserResponse createUser(UserRequest userRequest);

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return the UserResponse DTO.
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves all users.
     *
     * @return a list of UserResponse DTOs.
     */
    List<UserResponse> getAllUsers();

    /**
     * Updates an existing user.
     *
     * @param id          the unique identifier of the user to update.
     * @param userRequest the DTO containing updated user information.
     * @return the updated UserResponse DTO.
     */
    UserResponse updateUser(Long id, UserRequest userRequest);

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the unique identifier of the user to delete.
     */
    void deleteUser(Long id);

}
