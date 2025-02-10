package com.epam.spring.controller;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.JwtAuthenticationResponse;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.LoginAttemptException;
import com.epam.spring.service.LoginAttemptService;
import com.epam.spring.service.impl.UserService;
import com.epam.spring.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final UserDetailsService userDetailsService;

    @PatchMapping("/activate/{username}/status")
    public ResponseEntity<Void> activateProfile(@PathVariable("username") String username) {
        userService.activateProfile(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/login/password")
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody CredentialChangeRequestDTO request) {
        userService.changeCredentials(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody UserCredentialsRequestDTO request) {
        String username = request.getUsername();
        if (loginAttemptService.isBlocked(username)) {
            throw new LoginAttemptException();
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            loginAttemptService.resetAttempts(username);
            return ResponseEntity.ok().body(jwtUtil.generateToken(userDetails));
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(username);
            throw new IncorrectCredentialsException();
        }
    }
}
