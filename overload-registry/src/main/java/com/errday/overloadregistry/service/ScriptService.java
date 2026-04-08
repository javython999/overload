package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.script.ScriptFileSaveResponse;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class ScriptService {

    private final FileService fileService;
    private final ScriptRepository scriptRepository;

    public Script Save(Load load, MultipartFile scriptFile) throws IOException {

        ScriptFileSaveResponse scriptFileSaveResponse = fileService.saveScriptFile(load, scriptFile);

        Script script = Script.builder()
                .originFileName(scriptFileSaveResponse.originFileName())
                .saveFileName(scriptFileSaveResponse.saveFileName())
                .savePath(scriptFileSaveResponse.savePath())
                .build();
        script.setLoad(load);

        return scriptRepository.save(script);
    }

    @Transactional(readOnly = true)
    public Script findById(long id) {
        return scriptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Script not found: " + id));
    }

    @Transactional(readOnly = true)
    public Script findByLoadId(long loadId) {
        return scriptRepository.findByLoadId(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Script not found by load id : " + loadId));
    }
}
