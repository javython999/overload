package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.repository.AttacheFileRepository;
import com.errday.overloadregistry.repository.LoadRepository;
import com.errday.overloadregistry.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DownloadOrchestration {

    private final FileService fileService;
    private final ScriptRepository scriptRepository;
    private final AttacheFileRepository attacheFileRepository;
    private final LoadRepository loadRepository;

    public Resource downloadFile(String saveFileName) throws MalformedURLException {
        String savePath = getSavePath(saveFileName);

        Path filePath = Paths.get(savePath);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IllegalArgumentException("Could not read file: " + saveFileName);
        }
    }

    public String getOriginFileName(String saveFileName) {
        return scriptRepository.findBySaveFileName(saveFileName)
                .map(Script::getOriginFileName)
                .or(() -> attacheFileRepository.findBySaveFileName(saveFileName)
                        .map(AttacheFile::getOriginFileName))
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + saveFileName));
    }

    private String getSavePath(String saveFileName) {
        return scriptRepository.findBySaveFileName(saveFileName)
                .map(Script::getSavePath)
                .or(() -> attacheFileRepository.findBySaveFileName(saveFileName)
                        .map(AttacheFile::getSavePath))
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + saveFileName));
    }

    public Resource downloadZip(Long loadId) throws IOException {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));

        List<Path> pathsToZip = new ArrayList<>();

        Script script = scriptRepository.findByLoadId(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Script not found by load id: " + loadId));

        String scriptFileSaveFullPath = script.getSavePath() + "/" + script.getSaveFileName();
        pathsToZip.add(fileService.getPath(scriptFileSaveFullPath));

        for (AttacheFile attacheFile : load.getAttacheFiles()) {
            pathsToZip.add(fileService.getPath(attacheFile.getSavePath() + "/" + attacheFile.getSaveFileName()));
        }

        Path zipPath = Files.createTempFile("load_" + loadId + "_", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (Path path : pathsToZip) {
                if (!Files.exists(path)) continue;

                String entryName = path.getFileName().toString();

                if (path.equals(fileService.getPath(scriptFileSaveFullPath))) {
                    entryName = script.getOriginFileName();
                } else {
                    for (AttacheFile af : load.getAttacheFiles()) {
                        String attachFileSaveFullPath = af.getSavePath() + "/" + af.getSaveFileName();
                        if (path.equals(fileService.getPath(attachFileSaveFullPath))) {
                            entryName = af.getOriginFileName();
                            break;
                        }
                    }
                }

                zos.putNextEntry(new ZipEntry(entryName));
                Files.copy(path, zos);
                zos.closeEntry();
            }
        }

        return new FileSystemResource(zipPath.toFile());
    }
}
