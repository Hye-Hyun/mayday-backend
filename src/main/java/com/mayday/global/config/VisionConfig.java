package com.mayday.global.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
@Profile("!test")
public class VisionConfig {

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "google.application.credentials")
    public ImageAnnotatorClient imageAnnotatorClient(
            @Value("${google.application.credentials}") String credentialsPath
    ) throws IOException {
        try (InputStream credentialsStream = Files.newInputStream(Path.of(credentialsPath))) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            if (credentials.createScopedRequired()) {
                credentials = credentials.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            }

            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            return ImageAnnotatorClient.create(settings);
        }
    }
}
