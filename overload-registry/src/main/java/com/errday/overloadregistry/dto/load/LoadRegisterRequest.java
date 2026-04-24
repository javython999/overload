package com.errday.overloadregistry.dto.load;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record LoadRegisterRequest(
        String loadName,
        MultipartFile scriptFile,
        List<MultipartFile> attacheFiles
) {

    public boolean hasAttacheFiles() {
        return attacheFiles != null && attacheFiles.stream().anyMatch(f -> !f.isEmpty());
    }
}
