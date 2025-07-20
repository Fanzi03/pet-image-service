package com.example.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.entity.Pet;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel; 
import lombok.experimental.FieldDefaults;

@EnableKafka
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumerConfig {
    
    String KAFKA_BOOTSTRAP_SERVICES;
    String KAFKA_CONSUMER_GROUP_ID;

    public KafkaConsumerConfig(
        @Value("${SPRING_KAFKA_BOOTSTRAP_SERVICES}") String KAFKA_BOOTSTRAP_SERVICES,
        @Value("${SPRING_KAFKA_CONSUMER_GROUP_ID}") String KAFKA_CONSUMER_GROUP_ID
    ){
        this.KAFKA_BOOTSTRAP_SERVICES = KAFKA_BOOTSTRAP_SERVICES;
        this.KAFKA_CONSUMER_GROUP_ID = KAFKA_CONSUMER_GROUP_ID; 
    }

    @Bean
    public ConsumerFactory<String, Pet> consumerFactory(@Qualifier("kafkaObjectMapper") ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVICES);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONSUMER_GROUP_ID); 
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Pet> jsonDeserializer =
                new JsonDeserializer<>(Pet.class, objectMapper);
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Pet> 
        kafkaListenerContainerFactory(
        ConsumerFactory<String,Pet> consumerFactory
    ){
        var containerFactory = new 
            ConcurrentKafkaListenerContainerFactory<String,Pet>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
