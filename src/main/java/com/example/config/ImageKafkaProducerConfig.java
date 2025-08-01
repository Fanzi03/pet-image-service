package com.example.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageKafkaProducerConfig {

    String KAFKA_BOOTSTRAP_SERVICES;

    public ImageKafkaProducerConfig(
        @Value("${SPRING_KAFKA_BOOTSTRAP_SERVICES}") String KAFKA_BOOTSTRAP_SERVICES
    ){
        this.KAFKA_BOOTSTRAP_SERVICES = KAFKA_BOOTSTRAP_SERVICES;
    }

    @Bean("imageKafkaTemplate")
    public KafkaTemplate<String, String> imageKafkaTemplate(){
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVICES);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProperties));
    }

}
