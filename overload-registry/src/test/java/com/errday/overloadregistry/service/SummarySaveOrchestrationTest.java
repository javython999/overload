package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.summary.SummarySaveRequestDto;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Summary;
import com.errday.overloadregistry.enums.LoadStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummarySaveOrchestrationTest {

    @Mock
    private LoadService loadService;

    @Mock
    private SummaryService summaryService;

    @InjectMocks
    private SummarySaveOrchestration orchestration;

    @Test
    void saveSummary_savesSummaryWithCorrectLoadAndData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SummarySaveRequestDto dto = new SummarySaveRequestDto();
        dto.setLoadId(1L);
        dto.setSummary(mapper.readTree("{\"vus\":10}"));

        Load load = Load.builder().id(1L).status(LoadStatus.RUNNING).build();
        given(loadService.findById(1L)).willReturn(load);
        given(summaryService.save(any(Summary.class))).willAnswer(inv -> inv.getArgument(0));

        orchestration.saveSummary(dto);

        ArgumentCaptor<Summary> captor = ArgumentCaptor.forClass(Summary.class);
        verify(summaryService).save(captor.capture());
        Summary saved = captor.getValue();
        assertThat(saved.getLoad()).isSameAs(load);
        assertThat(saved.getSummaryData()).contains("vus");
    }
}