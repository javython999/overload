package com.errday.overloadworker.service;

import com.errday.overloadworker.dto.LoadStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendStatus(LoadStatusDto loadStatusDto) {
        try {
            String message = objectMapper.writeValueAsString(loadStatusDto);
            log.info("Sending status message: {}", message);
            kafkaTemplate.send("load-status", message);
        } catch (Exception e) {
            log.error("Failed to send status message for load: {}", loadStatusDto.getLoadId(), e);
        }
    }

    public void sendResult(Long loadId, String summaryJson) {
        try {
            log.info("Sending result message for loadId: {}", loadId);
            kafkaTemplate.send("load-result", "{\"loadId\": %d, \"summary\": %s}".formatted(loadId, summaryJson));
        } catch (Exception e) {
            log.error("Failed to send result message for load: {}", loadId, e);
        }
    }
}
