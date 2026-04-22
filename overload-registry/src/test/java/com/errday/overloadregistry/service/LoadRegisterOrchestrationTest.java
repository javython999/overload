package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadRegisterRequest;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.enums.LoadStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadRegisterOrchestrationTest {

    @Mock
    private LoadService loadService;

    @Mock
    private ScriptService scriptService;

    @Mock
    private AttacheFileService attacheFileService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private LoadRegisterOrchestration orchestration;

    @Test
    void save_withoutAttacheFiles_skipsAttacheFileService() throws IOException {
        MultipartFile scriptFile = new MockMultipartFile("script", "test.js", "text/plain", "k6".getBytes());
        // empty list → hasAttacheFiles() returns true due to reduce identity, but no files to save
        // Use a single empty file to force hasAttacheFiles() = false
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        LoadRegisterRequest request = new LoadRegisterRequest("my-load", scriptFile, List.of(emptyFile));

        Load savedLoad = Load.builder().id(1L).loadName("my-load").status(LoadStatus.REGISTERED).build();
        Script savedScript = Script.builder().id(10L).originFileName("test.js").build();
        given(loadService.save(request)).willReturn(savedLoad);
        given(scriptService.Save(savedLoad, scriptFile)).willReturn(savedScript);

        long loadId = orchestration.save(request);

        assertThat(loadId).isEqualTo(1L);
        verify(attacheFileService, never()).save(any(), any());
        verify(kafkaProducerService).send(savedLoad, savedScript);
    }

    @Test
    void save_withAttacheFiles_callsAttacheFileService() throws IOException {
        MultipartFile scriptFile = new MockMultipartFile("script", "test.js", "text/plain", "k6".getBytes());
        MultipartFile attacheFile = new MockMultipartFile("file", "data.csv", "text/plain", "col1,col2".getBytes());
        LoadRegisterRequest request = new LoadRegisterRequest("my-load", scriptFile, List.of(attacheFile));

        Load savedLoad = Load.builder().id(1L).loadName("my-load").status(LoadStatus.REGISTERED).build();
        Script savedScript = Script.builder().id(10L).originFileName("test.js").build();
        given(loadService.save(request)).willReturn(savedLoad);
        given(scriptService.Save(savedLoad, scriptFile)).willReturn(savedScript);

        long loadId = orchestration.save(request);

        assertThat(loadId).isEqualTo(1L);
        verify(attacheFileService).save(eq(savedLoad), eq(List.of(attacheFile)));
        verify(kafkaProducerService).send(savedLoad, savedScript);
    }
}