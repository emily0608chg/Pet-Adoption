package com.petadoption.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.dto.auth.LoginDTO;
import com.petadoption.dto.auth.RegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Crear un DTO válido para la prueba
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("new_user");
        registerDTO.setPassword("password123");
        registerDTO.setName("Test User");
        registerDTO.setEmail("new.user@example.com");
        registerDTO.setPhone("123456789");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated()) // Respuesta esperada: 201 Created
                .andExpect(jsonPath("$.id").exists()) // Verificar que el ID del usuario sea parte de la respuesta
                .andExpect(jsonPath("$.username").value("new_user"))
                .andExpect(jsonPath("$.roles").isArray()); // Los roles del usuario deben ser devueltos en un array
    }

    @Test
    void shouldFailToRegisterUserWithInvalidData() throws Exception {
        // Crear un DTO con datos inválidos
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(""); // Vacío: debería fallar por @NotBlank
        registerDTO.setPassword("short"); // Menos de 8 caracteres
        registerDTO.setName(""); // Vacío
        registerDTO.setEmail("invalid_email"); // Formato inválido
        registerDTO.setPhone(""); // Vacío

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest()) // Respuesta esperada: 400 Bad Request
                .andExpect(jsonPath("$.username").value("El nombre de usuario es obligatorio.")) // Validar el error específico de username
                .andExpect(jsonPath("$.password").value("La contraseña debe tener al menos 8 caracteres.")) // Validar el error específico de password
                .andExpect(jsonPath("$.name").value("El nombre es obligatorio.")) // Validar el error específico de name
                .andExpect(jsonPath("$.email").value("Debe proporcionar un correo electrónico válido.")) // Validar el error específico de email
                .andExpect(jsonPath("$.phone").value("El número de teléfono es obligatorio.")); // Validar el error específico de phone
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        // Crear credenciales de usuario válidas
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("salome");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk()) // Respuesta esperada: 200 OK
                .andExpect(jsonPath("$.accessToken").exists()) // El accessToken debe estar presente en la respuesta
                .andExpect(jsonPath("$.refreshToken").exists()); // El refreshToken debe estar presente en la respuesta
    }

    @Test
    void shouldFailToLoginWithInvalidData() throws Exception {
        // Login con credenciales inválidas
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent_user");
        loginDTO.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized()) // Respuesta esperada: 401 Unauthorized
                .andExpect(jsonPath("$.error").value("User not found with username: nonexistent_user")); // Cambiar para reflejar el mensaje real
    }
}