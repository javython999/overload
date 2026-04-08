package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.enums.LoadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoadRetryOrchestration {

    private final LoadService loadService;
    private final ScriptService scriptService;
    private final KafkaProducerService kafkaProducerService;

    public void retry(Long loadId) {
        Load load = loadService.findById(loadId);
        loadService.updateStatus(loadId, LoadStatus.REGISTERED);
        Script script = scriptService.findByLoadId(loadId);
        kafkaProducerService.send(load, script);
    }
}
