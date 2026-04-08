package com.errday.overloadregistry.dto.load;

import com.errday.overloadregistry.dto.load.attachefile.AttachFileResponse;
import com.errday.overloadregistry.dto.load.script.ScriptResponse;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;

public record LoadDetailResponse(
        long loadId,
        String loadName,
        String statusText,
        String statusClassName,
        String scriptContent,
        @JsonRawValue
        String summary,
        ScriptResponse script,
        List<AttachFileResponse> attacheFiles
) {
}
