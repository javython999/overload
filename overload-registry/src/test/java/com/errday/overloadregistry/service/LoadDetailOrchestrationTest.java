package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadDetailResponse;
import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.entity.Summary;
import com.errday.overloadregistry.enums.LoadStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadDetailOrchestrationTest {

    @Mock
    private LoadService loadService;

    @Mock
    private ScriptService scriptService;

    @Mock
    private AttacheFileService attacheFileService;

    @Mock
    private FileService fileService;

    @Mock
    private SummaryService summaryService;

    @InjectMocks
    private LoadDetailOrchestration orchestration;

    @Test
    void findById_whenCompleted_includesSummaryData() throws IOException {
        Load load = Load.builder().id(1L).loadName("test").status(LoadStatus.COMPLETED).build();
        Script script = Script.builder().id(10L).originFileName("test.js")
                .savePath("/load_1/script").saveFileName("uuid.js").build();
        Summary summary = Summary.builder().id(1L).summaryData("{\"vus\":10}").build();

        given(loadService.findById(1L)).willReturn(load);
        given(scriptService.findByLoadId(1L)).willReturn(script);
        given(attacheFileService.findByLoadId(1L)).willReturn(List.of());
        given(fileService.readScriptFile("/load_1/script/uuid.js")).willReturn("import http from 'k6/http';");
        given(summaryService.findByLoadId(1L)).willReturn(summary);

        LoadDetailResponse response = orchestration.findById(1L);

        assertThat(response.loadId()).isEqualTo(1L);
        assertThat(response.loadName()).isEqualTo("test");
        assertThat(response.statusText()).isEqualTo(LoadStatus.COMPLETED.getText());
        assertThat(response.scriptContent()).isEqualTo("import http from 'k6/http';");
        assertThat(response.summary()).isEqualTo("{\"vus\":10}");
        assertThat(response.attacheFiles()).isEmpty();
        verify(summaryService).findByLoadId(1L);
    }

    @Test
    void findById_whenNotCompleted_returnEmptySummary() throws IOException {
        Load load = Load.builder().id(2L).loadName("running").status(LoadStatus.RUNNING).build();
        Script script = Script.builder().id(20L).originFileName("test.js")
                .savePath("/load_2/script").saveFileName("uuid.js").build();
        AttacheFile attacheFile = AttacheFile.builder()
                .id(100L).originFileName("data.csv").saveFileName("uuid.csv")
                .savePath("/load_2/attache").load(load).build();

        given(loadService.findById(2L)).willReturn(load);
        given(scriptService.findByLoadId(2L)).willReturn(script);
        given(attacheFileService.findByLoadId(2L)).willReturn(List.of(attacheFile));
        given(fileService.readScriptFile("/load_2/script/uuid.js")).willReturn("script content");

        LoadDetailResponse response = orchestration.findById(2L);

        assertThat(response.summary()).isEqualTo("{}");
        assertThat(response.attacheFiles()).hasSize(1);
        verify(summaryService, never()).findByLoadId(any());
    }

    @Test
    void findById_whenCompleted_summaryIsNull_returnsEmptySummary() throws IOException {
        Load load = Load.builder().id(3L).loadName("done").status(LoadStatus.COMPLETED).build();
        Script script = Script.builder().id(30L).originFileName("test.js")
                .savePath("/load_3/script").saveFileName("uuid.js").build();

        given(loadService.findById(3L)).willReturn(load);
        given(scriptService.findByLoadId(3L)).willReturn(script);
        given(attacheFileService.findByLoadId(3L)).willReturn(List.of());
        given(fileService.readScriptFile("/load_3/script/uuid.js")).willReturn("content");
        given(summaryService.findByLoadId(3L)).willReturn(null);

        LoadDetailResponse response = orchestration.findById(3L);

        assertThat(response.summary()).isEqualTo("{}");
    }
}