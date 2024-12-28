package com.petadoption.data;

import com.petadoption.model.TypeOfPet;
import com.petadoption.repository.TypeOfPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final TypeOfPetRepository typeOfPetRepository;
//
//    @Autowired
//    public DataInitializer(TypeOfPetRepository typeOfPetRepository) {
//        this.typeOfPetRepository = typeOfPetRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Agregar tipos de mascotas si no existen
//        if (typeOfPetRepository.count() == 0) {
//            typeOfPetRepository.save(new TypeOfPet("Dog", 1L));
//            typeOfPetRepository.save(new TypeOfPet("Cat", 2L));
//            typeOfPetRepository.save(new TypeOfPet("Hamster", 3L));
//            typeOfPetRepository.save(new TypeOfPet("Tortoise", 4L));
//        }
//    }
//}
