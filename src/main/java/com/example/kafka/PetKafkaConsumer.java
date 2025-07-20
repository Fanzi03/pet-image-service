package com.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.entity.Pet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetKafkaConsumer {

    @KafkaListener(topics = "pets", groupId = "image-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumePet(Pet pet){
        log.info("Received pet: pet={}", pet);
    }

    
}
