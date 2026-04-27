package com.errday.overloadworker.service;

import com.errday.overloadworker.LoadStatus;
import com.errday.overloadworker.dto.LoadStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendStatus_sendsSerializedMessageToLoadStatusTopic() throws Exception {
        LoadStatusDto dto = new LoadStatusDto(1L, LoadStatus.RUNNING);
        given(objectMapper.writeValueAsString(dto)).willReturn("{\"loadId\":1,\"status\":\"RUNNING\"}");

        kafkaProducerService.sendStatus(dto);

        verify(kafkaTemplate).send(eq("load-status"), eq("{\"loadId\":1,\"status\":\"RUNNING\"}"));
    }

    @Test
    void sendStatus_doesNotThrow_whenSerializationFails() throws Exception {
        LoadStatusDto dto = new LoadStatusDto(1L, LoadStatus.RUNNING);
        given(objectMapper.writeValueAsString(dto)).willThrow(new JsonProcessingException("error") {});

        kafkaProducerService.sendStatus(dto);

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void sendResult_sendsFormattedMessageToLoadResultTopic() {
        kafkaProducerService.sendResult(1L, "{\"overview\":{}}");

        verify(kafkaTemplate).send(eq("load-result"), eq("{\"loadId\": 1, \"summary\": {\"overview\":{}}}"));
    }

    @Test
    void sendResult_doesNotThrow_whenKafkaTemplateFails() {
        given(kafkaTemplate.send(anyString(), any())).willThrow(new RuntimeException("kafka error"));

        kafkaProducerService.sendResult(1L, "{\"overview\":{}}");

        verify(kafkaTemplate).send(eq("load-result"), anyString());
    }
}
