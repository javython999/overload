package com.errday.overloadregistry.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SummaryTest {

    @Test
    void getSummaryDataAsJson_parsesValidJson() {
        Summary summary = Summary.builder()
                .summaryData("{\"vus\": 10, \"http_req_duration\": 200}")
                .build();

        JsonNode json = summary.getSummaryDataAsJson();

        assertThat(json.get("vus").asInt()).isEqualTo(10);
        assertThat(json.get("http_req_duration").asInt()).isEqualTo(200);
    }

    @Test
    void getSummaryDataAsJson_throwsRuntimeException_whenInvalidJson() {
        Summary summary = Summary.builder()
                .summaryData("not-json")
                .build();

        assertThatThrownBy(summary::getSummaryDataAsJson)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to convert summaryData to JsonNode");
    }
}