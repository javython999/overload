package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.Summary;
import com.errday.overloadregistry.repository.SummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private SummaryRepository summaryRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void save_returnsSavedSummary() {
        Summary summary = Summary.builder().summaryData("{\"vus\":10}").build();
        Summary saved = Summary.builder().id(1L).summaryData("{\"vus\":10}").build();
        given(summaryRepository.save(summary)).willReturn(saved);

        Summary result = summaryService.save(summary);

        assertThat(result.getId()).isEqualTo(1L);
        verify(summaryRepository).save(summary);
    }

    @Test
    void findByLoadId_returnsSummary() {
        Summary summary = Summary.builder().id(1L).summaryData("{}").build();
        given(summaryRepository.findByLoadId(5L)).willReturn(summary);

        Summary result = summaryService.findByLoadId(5L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(summaryRepository).findByLoadId(5L);
    }

    @Test
    void findByLoadId_returnsNull_whenNotFound() {
        given(summaryRepository.findByLoadId(99L)).willReturn(null);

        Summary result = summaryService.findByLoadId(99L);

        assertThat(result).isNull();
    }
}