package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.summary.SummarySaveRequestDto;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Summary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SummarySaveOrchestration {

    private final LoadService loadService;
    private final SummaryService summaryService;

    public void saveSummary(SummarySaveRequestDto resultDto) {
        Load load = loadService.findById(resultDto.getLoadId());

        Summary summary = Summary.builder()
                .summaryData(resultDto.getSummary().toString())
                .load(load)
                .build();

        summaryService.save(summary);
        log.info("Saved load result summary for loadId: {}", resultDto.getLoadId());
    }
}
