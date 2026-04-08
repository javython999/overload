package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.attachefile.AttacheFileSaveResponse;
import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.repository.AttacheFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AttacheFileService {

    private final FileService fileService;
    private final AttacheFileRepository attacheFileRepository;

    public List<AttacheFile> save(Load load, List<MultipartFile> attacheFiles) throws IOException {
        List<AttacheFile> result = new ArrayList<>();

        for (MultipartFile attacheFile : attacheFiles) {
            AttacheFileSaveResponse response = fileService.saveAttacheFile(load, attacheFile);

            AttacheFile entity = AttacheFile.builder()
                    .originFileName(response.originFileName())
                    .saveFileName(response.saveFileName())
                    .savePath(response.savePath())
                    .build();
            load.addAttacheFile(entity);
            result.add(entity);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<AttacheFile> findByLoadId(long loadId) {
        return attacheFileRepository.findByLoadId(loadId);
    }

}
