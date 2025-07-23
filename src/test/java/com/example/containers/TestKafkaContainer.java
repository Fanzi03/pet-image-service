package com.example.containers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.example.entity.Pet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:.env")
@Testcontainers
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestKafkaContainer {

 @Container
 static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest")
  .asCompatibleSubstituteFor("apache/kafka"));
    
 @Autowired
 private KafkaTemplate<String, String> kafkaTemplate;
    
 @Test
 public void getPetFromKafka() throws InterruptedException {
  Properties producerProps = new Properties();
  producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
  producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
  producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

  KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProps);
  
  String petJson = "{\"id\": 1, \"name\": \"Buddy\", \"type\": \"dog\", \"gender\": \"male\", \"age\": 5}";
  kafkaProducer.send(new ProducerRecord<String,String>("pets", petJson));
  kafkaProducer.close();
        
  Thread.sleep(2000);
        
  assertTrue(true);  
 }

  @KafkaListener(topics = "pets")
  public void handlePet(Pet pet){
    log.info("pet: {}", pet);
  }
}

