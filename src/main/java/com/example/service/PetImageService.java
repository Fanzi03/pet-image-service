package com.example.service;

import org.springframework.stereotype.Service;
import com.example.entity.Pet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PetImageService {
  
  public String petToPromt(Pet pet){
    return "Please generate for me the pet with these characteristics, name: " + pet.getName() 
      + ", age: " + pet.getAge() + ", type: " + pet.getType() + ", gender: " + pet.getGender(); 
  }
    
}
