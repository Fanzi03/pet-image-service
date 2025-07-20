package com.example.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PetTest {
    
    @Test
    void createPet(){
        Pet pet = Pet.builder().build();

        
    }

    
}
