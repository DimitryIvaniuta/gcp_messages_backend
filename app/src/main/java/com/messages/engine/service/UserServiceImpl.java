package com.messages.engine.service;

import com.messages.engine.dto.UserRequest;
import com.messages.engine.dto.UserResponse;
import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.User;
import com.messages.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService to manage user operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Repository for User entities.
     */
    private final UserRepository userRepository;

    /**
     * Creates a new user based on the provided UserRequest.
     *
     * @param userRequest the DTO containing new user's information.
     * @return the created UserResponse DTO.
     */
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Build a new User entity using Lombok's builder pattern.
        User user = new User();//.builder()
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
//                .username(userRequest.username())
//                .email(userRequest.email())
//                .password(userRequest.password())
//                .build();

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return the corresponding UserResponse DTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserResponse(user.getId(),
                user.getLogin(),
                user.getUserName(),
                user.getEmail(),
                user.getCreatedAt());
    }

    /**
     * Retrieves all users.
     *
     * @return a list of UserResponse DTOs.
     */
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(),
                        user.getLogin(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getCreatedAt()))
                .toList();
    }

    /**
     * Updates an existing user with the provided information.
     *
     * @param id          the unique identifier of the user to update.
     * @param userRequest the DTO containing updated user information.
     * @return the updated UserResponse DTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update the fields.
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser.getId(),
                updatedUser.getLogin(),
                updatedUser.getUserName(),
                updatedUser.getEmail(),
                updatedUser.getCreatedAt());
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the unique identifier of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

}
