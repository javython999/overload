package com.errday.overloadregistry.dto.kafka;

import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KafkaProduceDto {
    private Long loadId;
    private String loadName;
    private String scriptFileName;
    private List<String> attacheFileNames;

    public KafkaProduceDto(Load load, Script script) {
        this.loadId = load.getId();
        this.loadName = load.getLoadName();
        this.scriptFileName = script.getOriginFileName();
        if (load.getAttacheFiles() != null) {
            this.attacheFileNames = load.getAttacheFiles().stream()
                    .map(com.errday.overloadregistry.entity.AttacheFile::getSaveFileName)
                    .toList();
        }
    }
}
