package com.errday.overloadworker.dto;

import com.errday.overloadworker.LoadStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoadStatusDto {
    private Long loadId;
    private LoadStatus status;
}
