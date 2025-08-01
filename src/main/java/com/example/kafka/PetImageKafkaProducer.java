package com.example.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PetImageKafkaProducer {
   KafkaTemplate<String, String> imageKafkaTemplate;
  
   public void sendPetImageToKafka(String imageUrl){
    String key = "imageUrl-" + imageUrl;
    imageKafkaTemplate.send(
      "urls", key, imageUrl
    );
   }

}
