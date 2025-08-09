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
		try {
			String imageUrl = imageService.generateImage(prompt);
			log.info("generateImage returned for pet {} -> {}", pet.getId(), imageUrl);
			if (imageUrl == null) {
				log.warn("Image URL is null for pet {} (prompt: {})", pet.getId(), prompt);
				petImageKafkaProducer.sendPetImageToKafka(pet.getId(), "no-image");
			} else {
				petImageKafkaProducer.sendPetImageToKafka(pet.getId(), imageUrl);
			}
		} catch (Exception e) {
			log.error("Failed to process pet {}: ", pet.getId(), e);
			petImageKafkaProducer.sendPetImageToKafka(pet.getId(), "generate-failed: " + e.toString());
		}

	}
}
