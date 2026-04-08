package com.errday.overloadregistry.dto.load.script;

public record ScriptFileSaveResponse(
        String originFileName,
        String saveFileName,
        String savePath
) {
}
