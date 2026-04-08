package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.kafka.KafkaProduceDto;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(Load load, Script script) {
        try {
            KafkaProduceDto kafkaProduceDto = new KafkaProduceDto(load, script);
            String message = objectMapper.writeValueAsString(kafkaProduceDto);
            log.info("Sending Register message: {}", message);
            kafkaTemplate.send("load-test", String.valueOf(load.getId()), message);
        } catch (Exception e) {
            log.error("Failed to send Kafka message for load: {}", load.getId(), e);
        }
    }
}
