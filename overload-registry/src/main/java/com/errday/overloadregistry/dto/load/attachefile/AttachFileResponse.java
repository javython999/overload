package com.errday.overloadregistry.dto.load.attachefile;

import com.errday.overloadregistry.entity.AttacheFile;

import java.time.LocalDateTime;

public record AttachFileResponse(
        long id,
        long loadId,
        String originFileName,
        String saveFileName,
        String savePath,
        LocalDateTime createAt
) {
    public static AttachFileResponse from(AttacheFile attacheFile) {
        return new AttachFileResponse(
                attacheFile.getId(),
                attacheFile.getLoad().getId(),
                attacheFile.getOriginFileName(),
                attacheFile.getSaveFileName(),
                attacheFile.getSavePath(),
                attacheFile.getCreateAt()
        );
    }
}
