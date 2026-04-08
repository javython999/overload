package com.errday.overloadregistry.dto.load.summary;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummarySaveRequestDto {
    private Long loadId;
    private JsonNode summary;
}
