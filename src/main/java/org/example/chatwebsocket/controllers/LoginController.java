package org.example.chatwebsocket.controllers;

import lombok.AllArgsConstructor;
import org.example.chatwebsocket.models.LoginRequest;
import org.example.chatwebsocket.models.LoginResponse;
import org.example.chatwebsocket.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private UserService userService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        if(userService.loginWithUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword()) == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(new LoginResponse(loginRequest.getUsername()));
        }
    }
}
