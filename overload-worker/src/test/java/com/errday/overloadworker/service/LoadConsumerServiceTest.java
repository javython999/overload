package com.errday.overloadworker.service;

import com.errday.overloadworker.LoadStatus;
import com.errday.overloadworker.dto.LoadStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadConsumerServiceTest {

    @Mock
    private DownloadService downloadService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaListenerEndpointRegistry registry;

    @InjectMocks
    private LoadConsumerService loadConsumerService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loadConsumerService, "logPath", tempDir.resolve("logs").toString());
        ReflectionTestUtils.setField(loadConsumerService, "prometheusEndpoints", "http://localhost:9090/api/v1/write");
        ReflectionTestUtils.setField(loadConsumerService, "bundleScript", "/opt/k6/bundle.js");
    }

    @Test
    void consume_sendsRunningStatus_whenMessageIsValid() throws Exception {
        String message = "{\"loadId\":1,\"scriptFileName\":\"test.js\"}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, "test.js"));

        File loadDir = createLoadDirWithScript("test.js");
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willReturn(loadDir);

        loadConsumerService.consume(message);

        ArgumentCaptor<LoadStatusDto> captor = ArgumentCaptor.forClass(LoadStatusDto.class);
        verify(kafkaProducerService, atLeastOnce()).sendStatus(captor.capture());

        List<LoadStatusDto> statuses = captor.getAllValues();
        assertThat(statuses.get(0).getStatus()).isEqualTo(LoadStatus.RUNNING);
    }

    @Test
    void consume_sendsFailedStatus_whenDownloadFails() throws Exception {
        String message = "{\"loadId\":1,\"scriptFileName\":\"test.js\"}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, "test.js"));
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willThrow(new IOException("download failed"));

        loadConsumerService.consume(message);

        ArgumentCaptor<LoadStatusDto> captor = ArgumentCaptor.forClass(LoadStatusDto.class);
        verify(kafkaProducerService, atLeastOnce()).sendStatus(captor.capture());

        List<LoadStatusDto> statuses = captor.getAllValues();
        assertThat(statuses).extracting(LoadStatusDto::getStatus).contains(LoadStatus.FAILED);
    }

    @Test
    void consume_sendsFailedStatus_whenScriptFileNotFound() throws Exception {
        String message = "{\"loadId\":1,\"scriptFileName\":\"missing.js\"}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, "missing.js"));

        File loadDir = createEmptyLoadDir();
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willReturn(loadDir);

        loadConsumerService.consume(message);

        ArgumentCaptor<LoadStatusDto> captor = ArgumentCaptor.forClass(LoadStatusDto.class);
        verify(kafkaProducerService, atLeastOnce()).sendStatus(captor.capture());

        List<LoadStatusDto> statuses = captor.getAllValues();
        assertThat(statuses).extracting(LoadStatusDto::getStatus).contains(LoadStatus.FAILED);
    }

    @Test
    void consume_sendsFailedStatus_whenJsonParsingFails() throws Exception {
        String message = "invalid-json";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willThrow(new com.fasterxml.jackson.core.JsonProcessingException("parse error") {});

        loadConsumerService.consume(message);

        verify(kafkaProducerService, never()).sendStatus(any());
    }

    @Test
    void consume_doesNotSendResult_whenScriptFileNameIsNull() throws Exception {
        String message = "{\"loadId\":1}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, null));

        File loadDir = createEmptyLoadDir();
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willReturn(loadDir);

        loadConsumerService.consume(message);

        verify(kafkaProducerService, never()).sendResult(anyLong(), anyString());
    }

    @Test
    void consume_cleansUpLoadDirectory_afterProcessing() throws Exception {
        String message = "{\"loadId\":1,\"scriptFileName\":\"test.js\"}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, "test.js"));

        File loadDir = createLoadDirWithScript("test.js");
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willReturn(loadDir);

        loadConsumerService.consume(message);

        assertThat(loadDir).doesNotExist();
    }

    @Test
    void consume_cleansUpLoadDirectory_evenWhenExceptionOccurs() throws Exception {
        String message = "{\"loadId\":1,\"scriptFileName\":\"test.js\"}";
        given(objectMapper.readValue(eq(message), any(Class.class)))
                .willReturn(createConsumeDto(1L, "test.js"));

        File loadDir = createLoadDirWithScript("test.js");
        given(downloadService.downloadAndUnzipLoadFiles(1L)).willReturn(loadDir);
        doNothing()
                .doThrow(new RuntimeException("unexpected error"))
                .doNothing()
                .when(kafkaProducerService).sendStatus(any());

        loadConsumerService.consume(message);

        assertThat(loadDir).doesNotExist();
    }

    private com.errday.overloadworker.dto.KafkaConsumeDto createConsumeDto(Long loadId, String scriptFileName) {
        return com.errday.overloadworker.dto.KafkaConsumeDto.builder()
                .loadId(loadId)
                .loadName("test-load")
                .scriptFileName(scriptFileName)
                .attacheFileNames(List.of())
                .build();
    }

    private File createLoadDirWithScript(String scriptFileName) throws IOException {
        File loadDir = tempDir.resolve("load_dir").toFile();
        loadDir.mkdirs();
        Files.writeString(new File(loadDir, scriptFileName).toPath(), "export default function() {}");
        return loadDir;
    }

    private File createEmptyLoadDir() {
        File loadDir = tempDir.resolve("load_dir_empty").toFile();
        loadDir.mkdirs();
        return loadDir;
    }
}
