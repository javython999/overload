package com.errday.overloadregistry.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptTest {

    @Test
    void saveFullPath_combinesSavePathAndSaveFileName() {
        Script script = Script.builder()
                .savePath("/load_1/script")
                .saveFileName("uuid.js")
                .build();

        assertThat(script.saveFullPath()).isEqualTo("/load_1/script/uuid.js");
    }
}