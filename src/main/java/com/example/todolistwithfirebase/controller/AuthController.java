package com.example.todolistwithfirebase.controller;

import com.example.todolistwithfirebase.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(newUser.getEmail())
                    .setPassword(newUser.getPassword());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body("Failed to register user: " + e.getMessage());
        }
    }

}

