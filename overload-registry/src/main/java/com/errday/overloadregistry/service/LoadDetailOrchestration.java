package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.LoadDetailResponse;
import com.errday.overloadregistry.dto.load.attachefile.AttachFileResponse;
import com.errday.overloadregistry.dto.load.script.ScriptResponse;
import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.entity.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoadDetailOrchestration {

    private final LoadService loadService;
    private final ScriptService scriptService;
    private final AttacheFileService attacheFileService;
    private final FileService fileService;
    private final SummaryService summaryService;

    @Transactional(readOnly = true)
    public LoadDetailResponse findById(long loadId) throws IOException {
        Load load = loadService.findById(loadId);
        Script script = scriptService.findByLoadId(loadId);
        List<AttacheFile> attachFiles = attacheFileService.findByLoadId(loadId);
        String scriptContent = fileService.readScriptFile(script.saveFullPath());

        String summaryData = "{}";
        if (load.isCompleted()) {
            Summary summary = summaryService.findByLoadId(loadId);
            if (summary != null) {
                summaryData = summary.getSummaryData();
            }
        }

        return new LoadDetailResponse(
                load.getId(),
                load.getLoadName(),
                load.getStatus().getText(),
                load.getStatus().getClassName(),
                scriptContent,
                summaryData,
                ScriptResponse.from(script),
                attachFiles.stream()
                        .map(AttachFileResponse::from)
                        .toList()
        );
    }
}
