package com.mayday.global.config;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("!test")
public class VisionConfig {
    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "google.application.credentials")
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
        return ImageAnnotatorClient.create();
    }
}
