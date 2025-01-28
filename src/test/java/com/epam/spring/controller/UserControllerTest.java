package com.epam.spring.controller;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Captor
    private ArgumentCaptor<UserCredentialsRequestDTO> credentialsCaptor;
    @Captor
    private ArgumentCaptor<CredentialChangeRequestDTO> credentialChangeCaptor;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testActivateProfile() throws Exception {

        mockMvc.perform(patch("/api/v1/users/activate/status")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isOk());

        verify(userService).activateProfile(eq("testUser"));
    }

    @Test
    public void testChangeLogin() throws Exception {
        String oldPass = "oldPass";
        String newPass = "newPass";
        String username = "testUser";
        CredentialChangeRequestDTO request = new CredentialChangeRequestDTO(username, oldPass, newPass);

        mockMvc.perform(put("/api/v1/users/login/password")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).changeCredentials(credentialChangeCaptor.capture());

        CredentialChangeRequestDTO capturedRequest = credentialChangeCaptor.getValue();
        assertEquals(oldPass, capturedRequest.getOldPassword());
        assertEquals(newPass, capturedRequest.getNewPassword());
    }

    @Test
    public void testLogin() throws Exception {
        UserCredentialsRequestDTO request = new UserCredentialsRequestDTO("testUser", "password123");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).login(
                credentialsCaptor.capture()
        );

        UserCredentialsRequestDTO capturedRequest = credentialsCaptor.getValue();
        assertEquals("password123", capturedRequest.getPassword());
    }
}