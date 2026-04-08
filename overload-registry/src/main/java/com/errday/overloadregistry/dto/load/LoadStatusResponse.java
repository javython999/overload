package com.errday.overloadregistry.dto.load;

import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.enums.LoadStatus;

public record LoadStatusResponse(
        long loadId,
        LoadStatus status
) {
    public static LoadStatusResponse from(Load load) {
        return new LoadStatusResponse(
                load.getId(),
                load.getStatus()
        );
    }
}
