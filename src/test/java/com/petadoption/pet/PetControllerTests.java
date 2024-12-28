package com.petadoption.pet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.controller.PetController;
import com.petadoption.model.Pet;
import com.petadoption.model.TypeOfPet;
import com.petadoption.model.enums.PetStatus;
import com.petadoption.service.PetService;
import com.petadoption.service.TypeOfPetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(PetController.class)
class PetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private TypeOfPetService typeOfPetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "salome")
    void registerPet_successful() throws Exception {
        // Arrange
        TypeOfPet typeOfPet = new TypeOfPet("Dog", 1L);
        Pet pet = new Pet(null, "Buddy", 2, PetStatus.AVAILABLE, typeOfPet, "New York");

        Mockito.when(typeOfPetService.getTypeOfPetById(1L)).thenReturn(Optional.of(typeOfPet));
        Mockito.when(petService.create(any(Pet.class))).thenReturn(
                new Pet(1L, "Buddy", 2, PetStatus.AVAILABLE, typeOfPet, "New York")
        );

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.location").value("New York"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerPet_invalidTypeOfPet() throws Exception {
        Pet pet = new Pet(null, "Billy", 2, PetStatus.AVAILABLE, new TypeOfPet("Dog", 5L), "Barcelona");

        Mockito.when(typeOfPetService.getTypeOfPetById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getPetById_found() throws Exception {
        TypeOfPet typeOfPet = new TypeOfPet("Dog", 1L);
        Pet pet = new Pet(1L, "Billy", 2, PetStatus.AVAILABLE, typeOfPet, "Buenos Aires, Argentina");

        Mockito.when(petService.getPetById(1L)).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/api/pets/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Billy"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllAvailablePets() throws Exception {
        TypeOfPet typeOfPet = new TypeOfPet("Cat", 2L);
        Pet pet1 = new Pet(1L, "Reina", 3, PetStatus.AVAILABLE, typeOfPet, "Tachira");
        Pet pet2 = new Pet(2L, "Hera", 5, PetStatus.AVAILABLE, typeOfPet, "Canada");

        Mockito.when(petService.getAllAvailablePets()).thenReturn(Arrays.asList(pet1, pet2));

        mockMvc.perform(get("/api/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Reina"))
                .andExpect(jsonPath("$[1].location").value("Canada"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePet_successful() throws Exception {
        Mockito.doNothing().when(petService).deletePetById(1L);

        mockMvc.perform(delete("/api/pets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePet_notFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Pet not found")).when(petService).deletePetById(1L);

        mockMvc.perform(delete("/api/pets/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
