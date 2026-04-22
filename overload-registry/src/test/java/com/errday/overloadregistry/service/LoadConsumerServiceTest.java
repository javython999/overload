package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadStatusResponse;
import com.errday.overloadregistry.dto.load.summary.SummarySaveRequestDto;
import com.errday.overloadregistry.enums.LoadStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadConsumerServiceTest {

    @Mock
    private LoadService loadService;

    @Mock
    private SummarySaveOrchestration saveOrchestration;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LoadConsumerService loadConsumerService;

    @BeforeEach
    void setUp() {
        loadConsumerService = new LoadConsumerService(loadService, saveOrchestration, objectMapper);
    }

    @Test
    void consume_updatesLoadStatus_whenValidMessage() throws Exception {
        String message = objectMapper.writeValueAsString(new LoadStatusResponse(1L, LoadStatus.RUNNING));

        loadConsumerService.consume(message);

        verify(loadService).updateStatus(1L, LoadStatus.RUNNING);
    }

    @Test
    void consume_doesNotThrow_whenMessageIsInvalidJson() {
        assertThatCode(() -> loadConsumerService.consume("invalid-json"))
                .doesNotThrowAnyException();
        verify(loadService, never()).updateStatus(any(), any());
    }

    @Test
    void consumeResult_savesSummary_whenValidMessage() throws Exception {
        SummarySaveRequestDto dto = new SummarySaveRequestDto();
        dto.setLoadId(1L);
        dto.setSummary(objectMapper.readTree("{\"vus\":10}"));
        String message = objectMapper.writeValueAsString(dto);

        loadConsumerService.consumeResult(message);

        verify(saveOrchestration).saveSummary(any(SummarySaveRequestDto.class));
    }

    @Test
    void consumeResult_doesNotThrow_whenMessageIsInvalidJson() {
        assertThatCode(() -> loadConsumerService.consumeResult("not-json"))
                .doesNotThrowAnyException();
        verify(saveOrchestration, never()).saveSummary(any());
    }

    @Test
    void consumeResult_doesNotThrow_whenOrchestrationThrows() throws Exception {
        SummarySaveRequestDto dto = new SummarySaveRequestDto();
        dto.setLoadId(99L);
        dto.setSummary(objectMapper.readTree("{}"));
        String message = objectMapper.writeValueAsString(dto);
        doThrow(new RuntimeException("db error")).when(saveOrchestration).saveSummary(any());

        assertThatCode(() -> loadConsumerService.consumeResult(message))
                .doesNotThrowAnyException();
    }
}
