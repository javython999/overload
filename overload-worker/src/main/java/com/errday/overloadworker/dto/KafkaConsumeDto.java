package com.errday.overloadworker.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KafkaConsumeDto {
    private Long loadId;
    private String loadName;
    private String scriptFileName;
    private List<String> attacheFileNames;
}
