package com.example.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest()
@ActiveProfiles("disable-kafka")
@TestPropertySource(locations = "file:.env")
public class PetTest {
    
    @Test
    void createPet(){
        Pet pet = Pet.builder().build();
    }

    
}
