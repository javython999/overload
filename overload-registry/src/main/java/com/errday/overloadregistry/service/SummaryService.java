package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.Summary;
import com.errday.overloadregistry.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;

    public Summary save(Summary summary) {
        return summaryRepository.save(summary);
    }

    public Summary findByLoadId(Long loadId) {
        return summaryRepository.findByLoadId(loadId);
    }

}
