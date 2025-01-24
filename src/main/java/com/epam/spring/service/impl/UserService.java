package com.epam.spring.service.impl;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.UserNotFoundException;
import com.epam.spring.model.User;
import com.epam.spring.repository.impl.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserCredentialsResponseDTO changeCredentials(String username, CredentialChangeRequestDTO credentialChangeRequest) {
        User user = findUserOrThrowException(username);
        checkPassword(credentialChangeRequest.getOldPassword(), user);
        user.setPassword(credentialChangeRequest.getNewPassword());
        userRepository.update(user);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    public void login(String username, UserCredentialsRequestDTO userCredentialsRequest) {
        User user = findUserOrThrowException(username);
        checkPassword(userCredentialsRequest.getPassword(), user);
    }

    public void activateProfile(String username, UserActivationRequestDTO activationRequest) {
        User user = findUserOrThrowException(username);
        user.setActive(!user.isActive());
        userRepository.update(user);
    }

    public void authenticate(String username, String password) {
        User user = findUserOrThrowException(username);
        checkPassword(password, user);
    }

    private static void checkPassword(String password, User user) {
        if (!Objects.equals(user.getPassword(), password)) {
            throw new IncorrectCredentialsException("Incorrect password");
        }
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
