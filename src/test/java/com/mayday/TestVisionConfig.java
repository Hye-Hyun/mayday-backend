package com.mayday;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestVisionConfig {

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() {
        return Mockito.mock(ImageAnnotatorClient.class);
    }
}
