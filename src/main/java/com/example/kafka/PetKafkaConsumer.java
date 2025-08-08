package com.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.entity.Pet;
import com.example.service.ImageService;
import com.example.service.PetImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PetKafkaConsumer {
    PetImageService petImageService;
    ImageService imageService;
    PetImageKafkaProducer petImageKafkaProducer;

    @KafkaListener(topics = "pets", groupId = "image-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumePet(Pet pet){
      	String prompt = petImageService.petToPromt(pet); 
//	petImageKafkaProducer.sendPetImageToKafka(imageService.generateImageAsync(prompt));
    }
}
