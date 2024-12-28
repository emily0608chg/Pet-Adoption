package com.petadoption.adoption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.controller.AdoptionController;
import com.petadoption.customexceptions.adoption.AdoptionNotFoundException;
import com.petadoption.model.Adoption;
import com.petadoption.model.Pet;
import com.petadoption.model.User;
import com.petadoption.model.enums.PetStatus;
import com.petadoption.service.AdoptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionController.class)
public class AdoptionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdoptionService adoptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void approveAdoption_whenAdoptionFound_shouldReturnApproved() throws Exception {
        Long adoptionId = 1L;
        Adoption adoption = new Adoption();
        adoption.setStatus("APPROVED");
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setStatus(PetStatus.ADOPTED);
        adoption.setPet(pet);

        when(adoptionService.approveAdoption(adoptionId)).thenReturn(adoption);

        mockMvc.perform(post("/api/adoption/{id}/approve", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adoptionService, times(1)).approveAdoption(adoptionId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void approveAdoption_whenAdoptionNotFound_shouldReturnNotFound() throws Exception {
        Long adoptionId = 1L;
        when(adoptionService.approveAdoption(adoptionId)).thenThrow(new AdoptionNotFoundException("Adoption not found"));

        mockMvc.perform(post("/api/adoption/{id}/approve", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(adoptionService, times(1)).approveAdoption(adoptionId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void rejectAdoption_whenAdoptionFound_shouldReturnRejected() throws Exception {
        Long adoptionId = 1L;
        Adoption adoption = new Adoption();
        adoption.setStatus("REJECTED");

        when(adoptionService.rejectAdoption(adoptionId)).thenReturn(adoption);

        mockMvc.perform(post("/api/adoption/{id}/reject", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(adoptionService, times(1)).rejectAdoption(adoptionId);
    }

    @Test
    @WithMockUser(username = "salome")
    void registerAdoption_whenDataValid_shouldCreate() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("salome");

        Pet pet = new Pet();
        pet.setId(1L);

        Adoption adoption = new Adoption();
        adoption.setAdoptionId(1L);
        adoption.setUser(user);
        adoption.setPet(pet);
        adoption.setAdoptionDate(new Date());
        adoption.setStatus("PENDING");

        when(adoptionService.create(any(Adoption.class))).thenReturn(adoption);

        // Act & Assert
        mockMvc.perform(post("/api/adoption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoption))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adoptionId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(adoptionService, times(1)).create(any(Adoption.class));
    }

    @Test
    @WithMockUser(username = "user")
    void registerAdoption_whenValidationFails_shouldReturnBadRequest() throws Exception {
        Adoption invalidAdoption = new Adoption();

        mockMvc.perform(post("/api/adoption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAdoption))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("must not be blank"));

        verify(adoptionService, never()).create(any(Adoption.class));
    }

    @Test
    @WithMockUser(username = "user")
    void getAdoptionById_whenNotFound_shouldThrowAdoptionNotFoundException() throws Exception {
        // Arrange
        Long adoptionId = 100L;
        when(adoptionService.getAdoptionById(adoptionId)).thenThrow(new AdoptionNotFoundException("Adoption not found"));

        // Act & Assert
        mockMvc.perform(get("/api/adoption/{id}", adoptionId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(adoptionService, times(1)).getAdoptionById(adoptionId);
    }

    @Test
    @WithMockUser(username = "user")
    void getAdoptionById_whenAccessDenied_shouldReturnForbidden() throws Exception {
        Long adoptionId = 5L;
        when(adoptionService.getAdoptionById(adoptionId)).thenThrow(new AccessDeniedException("You are not authorized"));

        mockMvc.perform(get("/api/adoption/{id}", adoptionId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(adoptionService, times(1)).getAdoptionById(adoptionId);
    }
}