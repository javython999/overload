package com.errday.overloadregistry.dto.load.script;

import com.errday.overloadregistry.entity.Script;

import java.time.LocalDateTime;

public record ScriptResponse(
        long id,
        String originFileName,
        String saveFileName,
        LocalDateTime createAt
) {
    public static ScriptResponse from(Script script) {
        return new ScriptResponse(
                script.getId(),
                script.getOriginFileName(),
                script.getSaveFileName(),
                script.getCreateAt()
        );
    }
}
