package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entity.Pet;
import com.example.json.PetKafkaMixin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaObjectMapperConfiguration {
    
    @Bean("kafkaObjectMapper")
    ObjectMapper kafkaObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(Pet.class, PetKafkaMixin.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;

    }
}
