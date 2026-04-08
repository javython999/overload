package com.errday.overloadregistry.dto.load;

import com.errday.overloadregistry.entity.Load;

import java.time.LocalDateTime;

public record LoadListResponse(
        Long id,
        String loadName,
        String statusText,
        String statusClass,
        LocalDateTime createAt
) {
    public LoadListResponse(Load load) {
        this(
                load.getId(),
                load.getLoadName(),
                load.getStatus().getText(),
                load.getStatus().getClassName(),
                load.getCreateAt()
        );
    }
}