package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadStatusResponse;
import com.errday.overloadregistry.dto.load.summary.SummarySaveRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadConsumerService {

    private final LoadService loadService;
    private final SummarySaveOrchestration saveOrchestration;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "load-status", groupId = "overload-registry-group")
    public void consume(String message) {
        log.info("Received status message: {}", message);

        try {
            LoadStatusResponse loadStatusResponse = objectMapper.readValue(message, LoadStatusResponse.class);
            loadService.updateStatus(loadStatusResponse.loadId(), loadStatusResponse.status());
        } catch (Exception e) {
            log.error("Failed to process status message: {}", message, e);
        }
    }

    @KafkaListener(topics = "load-result", groupId = "overload-registry-group")
    public void consumeResult(String message) {
        log.info("Received result message: {}", message);

        try {
            SummarySaveRequestDto request = objectMapper.readValue(message, SummarySaveRequestDto.class);
            saveOrchestration.saveSummary(request);
        } catch (Exception e) {
            log.error("Failed to process result message: {}", message, e);
        }
    }
}
