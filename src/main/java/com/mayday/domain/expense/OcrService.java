package com.mayday.domain.expense;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.mayday.domain.expense.dto.OcrResultData;
import com.mayday.global.exception.OcrProcessingException;
import com.mayday.global.exception.UnsupportedImageTypeException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png");

    private final ObjectProvider<ImageAnnotatorClient> imageAnnotatorClientProvider;
    private final ReceiptTextParser receiptTextParser;
    private final WebClient.Builder webClientBuilder;

    @Value("${google.vision.api-key:}")
    private String googleVisionApiKey;

    public OcrResultData extract(MultipartFile file, Long userId) {
        validateImageType(file);

        String rawText = requestTextDetection(file);
        logExtractedTextForRetry(userId, rawText);

        ReceiptTextParser.ParsedReceipt parsed = receiptTextParser.parse(rawText);

        return OcrResultData.builder()
                .ocrId("ocr_" + UUID.randomUUID().toString().substring(0, 8))
                .rawText(rawText)
                .date(parsed.getDate())
                .merchantName(parsed.getMerchantName())
                .itemName(parsed.getItemName())
                .income(0)
                .expense(parsed.getExpense())
                .build();
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new UnsupportedImageTypeException("지원하지 않는 이미지 형식입니다");
        }
    }

    private String requestTextDetection(MultipartFile file) {
        ImageAnnotatorClient imageAnnotatorClient = imageAnnotatorClientProvider.getIfAvailable();
        if (imageAnnotatorClient != null) {
            log.debug("Requesting OCR with Google Vision client credentials");
            return requestTextDetectionWithClient(file, imageAnnotatorClient);
        }

        if (googleVisionApiKey != null && !googleVisionApiKey.isBlank()) {
            log.debug("Requesting OCR with Google Vision REST API key");
            return requestTextDetectionWithApiKey(file);
        }

        throw new OcrProcessingException("OCR 인증 정보가 설정되지 않았습니다");
    }

    private String requestTextDetectionWithClient(MultipartFile file, ImageAnnotatorClient imageAnnotatorClient) {
        try {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);
            AnnotateImageResponse res = response.getResponsesList().get(0);

            if (res.hasError()) {
                log.error("Vision API error: {}", res.getError().getMessage());
                throw new OcrProcessingException("OCR 처리 중 오류가 발생했습니다");
            }

            return res.getTextAnnotationsList().isEmpty()
                    ? ""
                    : res.getTextAnnotationsList().get(0).getDescription();

        } catch (IOException e) {
            log.error("이미지 읽기 실패", e);
            throw new OcrProcessingException("이미지 처리 중 오류가 발생했습니다", e);
        } catch (StatusRuntimeException e) {
            log.error("Vision API request failed. status={}, description={}",
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e);
            throw new OcrProcessingException("OCR 외부 서비스 인증 또는 요청 처리 중 오류가 발생했습니다", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String requestTextDetectionWithApiKey(MultipartFile file) {
        try {
            String encodedImage = Base64.getEncoder().encodeToString(file.getBytes());
            Map<String, Object> payload = Map.of(
                    "requests",
                    List.of(Map.of(
                            "image", Map.of("content", encodedImage),
                            "features", List.of(Map.of("type", "TEXT_DETECTION"))
                    ))
            );

            Map<String, Object> response = webClientBuilder.build()
                    .post()
                    .uri("https://vision.googleapis.com/v1/images:annotate?key={apiKey}", googleVisionApiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new OcrProcessingException("OCR 응답이 비어 있습니다");
            }

            List<Map<String, Object>> responses = (List<Map<String, Object>>) response.get("responses");
            if (responses == null || responses.isEmpty()) {
                throw new OcrProcessingException("OCR 응답이 비어 있습니다");
            }

            Map<String, Object> firstResponse = responses.get(0);
            if (firstResponse.containsKey("error")) {
                log.error("Vision REST API error: {}", firstResponse.get("error"));
                throw new OcrProcessingException("OCR 처리 중 오류가 발생했습니다");
            }

            List<Map<String, Object>> textAnnotations =
                    (List<Map<String, Object>>) firstResponse.get("textAnnotations");

            if (textAnnotations == null || textAnnotations.isEmpty()) {
                return "";
            }

            Object description = textAnnotations.get(0).get("description");
            return description == null ? "" : description.toString();
        } catch (IOException e) {
            log.error("이미지 읽기 실패", e);
            throw new OcrProcessingException("이미지 처리 중 오류가 발생했습니다", e);
        } catch (WebClientResponseException e) {
            String responseBody = sanitizeForLog(e.getResponseBodyAsString());
            log.error("Vision REST API request failed. status={}, body={}",
                    e.getStatusCode(),
                    responseBody);

            if (e.getStatusCode().is4xxClientError()) {
                throw new OcrProcessingException("OCR 외부 서비스 인증 또는 설정을 확인해주세요", e);
            }

            throw new OcrProcessingException("OCR 외부 서비스 요청 처리 중 오류가 발생했습니다", e);
        } catch (WebClientException e) {
            log.error("Vision REST API network request failed. message={}", sanitizeForLog(e.getMessage()));
            throw new OcrProcessingException("OCR 외부 서비스 요청 처리 중 오류가 발생했습니다", e);
        }
    }

    private String sanitizeForLog(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value
                .replaceAll("key=([^&\\s]+)", "key=***")
                .replaceAll("\"key\"\\s*:\\s*\"[^\"]+\"", "\"key\":\"***\"");
    }

    private void logExtractedTextForRetry(Long userId, String rawText) {
        log.info("[OCR_RETRY_LOG] userId={}, rawText={}", userId, rawText);
    }
}
