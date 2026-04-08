package com.errday.overloadregistry.dto.load;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record LoadRegisterRequest(
        String loadName,
        MultipartFile scriptFile,
        List<MultipartFile> attacheFiles
) {

    public boolean hasAttacheFiles() {
        return attacheFiles.stream()
                .map(f -> !f.isEmpty())
                .reduce(true, (a, b) -> a && b);
    }
}
