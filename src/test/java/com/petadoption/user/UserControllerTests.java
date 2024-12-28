package com.petadoption.user;

import com.petadoption.controller.UserController;
import com.petadoption.customexceptions.user.UserNotFoundException;
import com.petadoption.model.User;
import com.petadoption.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Correct `jsonPath`
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Test data
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "123456789", "john.doe@example.com", Set.of("ROLE_USER")),
                new User(2L, "Jane Smith", "987654321", "jane.smith@example.com", Set.of("ROLE_USER"))
        );

        // Mock the service behavior
        when(userService.getAllUsers()).thenReturn(users);

        // Perform the mock HTTP GET request
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()").value(2)) // Check response array size
                .andExpect(jsonPath("$[0].name").value("John Doe")) // Check the first user's name
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com")); // Check the second user's email

        // Verify that the service was called exactly once
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_UserExists_ShouldReturnUser() throws Exception {
        // Dato de prueba
        User user = new User(1L, "John Doe", "123456789", "john.doe@example.com", Set.of("ROLE_USER"));

        // Configuración del mock
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Simulamos la llamada
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificamos 200 OK
                .andExpect(jsonPath("$.name").value("John Doe")) // Validamos nombre
                .andExpect(jsonPath("$.email").value("john.doe@example.com")); // Validamos email
    }

    @Test
    void getUserById_UserDoesNotExist_ShouldReturn404() throws Exception {
        // Mock configurado para usuario no encontrado
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // Simulamos la llamada
        mockMvc.perform(get("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Validamos que el estado es 404
    }

    @Test
    void updateUser_UserExists_ShouldUpdateSuccessfully() throws Exception {
        // Datos de prueba
        User user = new User(1L, "John Doe Updated", "123456789", "john.doe@example.com", Set.of("ROLE_USER"));

        // Configuración del mock
        when(userService.updateUser(any(User.class))).thenReturn(user);

        // Simulamos la llamada al controlador
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "John Doe Updated",
                    "phone": "123456789",
                    "email": "john.doe@example.com"
                }
            """)) // Payload
                .andExpect(status().isOk()) // Estado 200
                .andExpect(jsonPath("$.name").value("John Doe Updated"));
    }

    @Test
    void updateUser_UserDoesNotExist_ShouldReturn404() throws Exception {
        // Configuración para simular excepción
        when(userService.updateUser(any(User.class))).thenThrow(new UserNotFoundException("User not found"));

        // Simulamos la llamada
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "John Doe Updated",
                    "phone": "123456789",
                    "email": "john.doe@example.com"
                }
            """))
                .andExpect(status().isNotFound()); // Estado 404
    }

    @Test
    void deleteUser_UserExists_ShouldReturnNoContent() throws Exception {
        // Simular eliminación sin excepción
        doNothing().when(userService).deleteUser(1L);

        // Simulamos la llamada
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent()); // Validamos estado 204
    }

    @Test
    void deleteUser_UserDoesNotExist_ShouldReturn404() throws Exception {
        // Simular excepción para usuario no encontrado
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(99L);

        // Simulamos la llamada
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound()); // Validamos estado 404
    }
}