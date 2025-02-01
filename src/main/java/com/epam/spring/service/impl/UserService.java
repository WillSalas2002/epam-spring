package com.epam.spring.service.impl;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.model.User;
import com.epam.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserCredentialsResponseDTO changeCredentials(CredentialChangeRequestDTO credentialChangeRequest) {
        User user = findUserOrThrowException(credentialChangeRequest.getUsername());
        checkPassword(credentialChangeRequest.getOldPassword(), user);
        user.setPassword(credentialChangeRequest.getNewPassword());
        userRepository.save(user);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    public void login(UserCredentialsRequestDTO userCredentialsRequest) {
        User user = findUserOrThrowException(userCredentialsRequest.getUsername());
        checkPassword(userCredentialsRequest.getPassword(), user);
    }

    public void activateProfile(String username) {
        User user = findUserOrThrowException(username);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void authenticate(String username, String password) {
        User user = findUserOrThrowException(username);
        checkPassword(password, user);
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    private static void checkPassword(String password, User user) {
        if (!Objects.equals(user.getPassword(), password)) {
            throw new IncorrectCredentialsException("Incorrect password");
        }
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
