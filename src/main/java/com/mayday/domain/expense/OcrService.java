package com.mayday.domain.expense;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.mayday.domain.expense.dto.OcrResultData;
import com.mayday.global.exception.OcrProcessingException;
import com.mayday.global.exception.UnsupportedImageTypeException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
        if (imageAnnotatorClient == null) {
            throw new OcrProcessingException("OCR 인증 정보가 설정되지 않았습니다");
        }

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

    private void logExtractedTextForRetry(Long userId, String rawText) {
        log.info("[OCR_RETRY_LOG] userId={}, rawText={}", userId, rawText);
    }
}
