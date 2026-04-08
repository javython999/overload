package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadRegisterRequest;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class LoadRegisterOrchestration {

    private final LoadService loadService;
    private final ScriptService scriptService;
    private final AttacheFileService attacheFileService;
    private final KafkaProducerService kafkaProducerService;

    public long save(LoadRegisterRequest request) throws IOException {
        Load load = loadService.save(request);
        Script script = scriptService.Save(load, request.scriptFile());

        if (request.hasAttacheFiles()) {
            attacheFileService.save(load, request.attacheFiles());
        }

        kafkaProducerService.send(load, script);
        return load.getId();
    }
}
