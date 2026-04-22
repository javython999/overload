package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.enums.LoadStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

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
    void send_sendsSerializedMessageToLoadTestTopic() throws Exception {
        Load load = Load.builder()
                .id(1L)
                .loadName("load-test")
                .status(LoadStatus.REGISTERED)
                .attacheFiles(List.of())
                .build();
        Script script = Script.builder()
                .id(10L)
                .originFileName("test.js")
                .build();
        given(objectMapper.writeValueAsString(any())).willReturn("{\"loadId\":1}");

        kafkaProducerService.send(load, script);

        verify(kafkaTemplate).send(eq("load-test"), eq("1"), eq("{\"loadId\":1}"));
    }

    @Test
    void send_doesNotThrowAndDoesNotSend_whenSerializationFails() throws Exception {
        Load load = Load.builder()
                .id(1L)
                .loadName("load-test")
                .status(LoadStatus.REGISTERED)
                .attacheFiles(List.of())
                .build();
        Script script = Script.builder().id(10L).originFileName("test.js").build();
        given(objectMapper.writeValueAsString(any())).willThrow(new RuntimeException("serialization error"));

        kafkaProducerService.send(load, script);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    void send_includesAttacheFileNamesInMessage() throws Exception {
        AttacheFile file = AttacheFile.builder().saveFileName("uuid.csv").build();
        Load load = Load.builder()
                .id(2L)
                .loadName("load-with-files")
                .status(LoadStatus.REGISTERED)
                .attacheFiles(List.of(file))
                .build();
        Script script = Script.builder().id(20L).originFileName("script.js").build();
        given(objectMapper.writeValueAsString(any())).willReturn("{\"loadId\":2}");

        kafkaProducerService.send(load, script);

        verify(kafkaTemplate).send(eq("load-test"), eq("2"), anyString());
    }
}