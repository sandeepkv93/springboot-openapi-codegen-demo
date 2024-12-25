package com.example.apiimpl.controller;

import com.example.api.UsersApi;
import com.example.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class UsersController implements UsersApi {
    private final List<User> users = new CopyOnWriteArrayList<>();

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users));
    }

    @Override
    public ResponseEntity<User> createUser(@Valid User user) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            return ResponseEntity.badRequest().build();
        }

        user.setId((long) (users.size() + 1));
        users.add(user);
        return ResponseEntity.status(201).body(user);
    }
}
