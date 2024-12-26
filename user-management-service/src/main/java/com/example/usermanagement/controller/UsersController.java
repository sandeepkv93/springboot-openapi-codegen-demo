package com.example.usermanagement.controller;

import com.example.api.UsersApi;
import com.example.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Demo implementation of the User Management API.
 * <p>
 * IMPORTANT: This is a demonstration controller that shows how to implement
 * an API interface generated from OpenAPI specifications. It uses a simple
 * in-memory list to store users, which is NOT suitable for production use.
 * <p>
 * Demo Features:
 * - Shows integration with OpenAPI generated interface
 * - Demonstrates basic CRUD operations
 * - Uses in-memory storage for simplicity
 * - Implements basic validation
 * <p>
 * Production Considerations (not implemented here):
 * - Should use proper persistence (database)
 * - Need proper error handling
 * - Should include security measures
 * - Should have proper logging
 * - Should include proper transaction management
 */
@RestController
public class UsersController implements UsersApi {

    // Using CopyOnWriteArrayList for thread-safety in this demo
    // In production, this would be replaced with a proper database repository
    private final List<User> users = new CopyOnWriteArrayList<>();

    /**
     * Retrieves all users from the in-memory storage.
     * <p>
     * Note: In a production environment, this should:
     * - Include pagination
     * - Have proper error handling
     * - Include security checks
     * - Use database querying
     */
    @Override
    public ResponseEntity<List<User>> getUsers() {
        // Returns a new ArrayList to prevent external modifications to the internal list
        return ResponseEntity.ok(new ArrayList<>(users));
    }

    /**
     * Creates a new user in the in-memory storage.
     * <p>
     * Demo Features:
     * - Basic email uniqueness check
     * - Simple ID generation
     * - Input validation via @Valid annotation
     * <p>
     * Note: In a production environment, this should:
     * - Use proper database persistence
     * - Include proper transaction management
     * - Have more robust error handling
     * - Include security measures
     * - Implement proper logging
     * - Have more sophisticated ID generation
     */
    @Override
    public ResponseEntity<User> createUser(@Valid User user) {
        // Demo implementation of email uniqueness check
        if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            return ResponseEntity.badRequest().build();
        }

        // Simple ID generation for demo purposes
        // In production, this would typically be handled by the database
        user.setId((long) (users.size() + 1));

        users.add(user);
        return ResponseEntity.status(201).body(user);
    }
}
