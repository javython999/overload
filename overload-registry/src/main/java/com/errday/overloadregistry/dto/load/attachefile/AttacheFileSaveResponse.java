package com.errday.overloadregistry.dto.load.attachefile;


public record AttacheFileSaveResponse(
        String originFileName,
        String saveFileName,
        String savePath
) {
}
