package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.attachefile.AttacheFileSaveResponse;
import com.errday.overloadregistry.dto.load.script.ScriptFileSaveResponse;
import com.errday.overloadregistry.entity.Load;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    public ScriptFileSaveResponse saveScriptFile(Load load, MultipartFile scriptFile) throws IOException {
        String originFileName = scriptFile.getOriginalFilename();
        String saveFileName = getSaveFileName(originFileName);
        String savePath = "/load_" + load.getId() + "/script";

        Path dirPath = Paths.get(uploadPath + savePath);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(saveFileName);
        scriptFile.transferTo(filePath);

        return new ScriptFileSaveResponse(originFileName, saveFileName, savePath);
    }

    public AttacheFileSaveResponse saveAttacheFile(Load load, MultipartFile attacheFile) throws IOException {
        String originFileName = attacheFile.getOriginalFilename();
        String saveFileName = getSaveFileName(originFileName);
        String savePath = "/load_" + load.getId() + "/attache";

        Path dirPath = Paths.get(uploadPath + savePath);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(saveFileName);
        attacheFile.transferTo(filePath);

        return new AttacheFileSaveResponse(originFileName, saveFileName, savePath);
    }

    @Transactional(readOnly = true)
    public Path getPath(String savePath) {
        return Path.of(uploadPath + savePath);
    }

    @Transactional(readOnly = true)
    public String readScriptFile(String savePath) throws IOException {
        return Files.readString(Paths.get(uploadPath + savePath));
    }

    private String getSaveFileName(String originFileName) {
        return UUID.randomUUID() + "." + FilenameUtils.getExtension(originFileName);
    }
}
