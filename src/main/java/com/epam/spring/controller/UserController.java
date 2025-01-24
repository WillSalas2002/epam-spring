package com.epam.spring.controller;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @PatchMapping("/activate/status")
    public ResponseEntity<Void> activateProfile(@RequestParam("username") String username, @Valid @RequestBody UserActivationRequestDTO request) {
        userService.activateProfile(username, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/login/password")
    public ResponseEntity<Void> changeLogin(@RequestParam("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        userService.changeCredentials(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam(value = "username") String username, @Valid @RequestBody UserCredentialsRequestDTO request) {
        userService.login(username, request);
        return ResponseEntity.ok().build();
    }
}
