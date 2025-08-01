package com.example.service;


import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageService {
  StabilityAiImageModel imageModel;
  
  public ImageResponse generateImage(String prompt){
    ImageResponse imageResponse = imageModel.call(
      new ImagePrompt(prompt)
    );

    return imageResponse;
  }
}
