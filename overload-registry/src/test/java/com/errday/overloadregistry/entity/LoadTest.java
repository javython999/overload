package com.errday.overloadregistry.entity;

import com.errday.overloadregistry.enums.LoadStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoadTest {

    @Test
    void isCompleted_whenStatusIsCompleted_returnsTrue() {
        Load load = Load.builder().status(LoadStatus.COMPLETED).build();
        assertThat(load.isCompleted()).isTrue();
    }

    @Test
    void isCompleted_whenStatusIsRegistered_returnsFalse() {
        Load load = Load.builder().status(LoadStatus.REGISTERED).build();
        assertThat(load.isCompleted()).isFalse();
    }

    @Test
    void isCompleted_whenStatusIsRunning_returnsFalse() {
        Load load = Load.builder().status(LoadStatus.RUNNING).build();
        assertThat(load.isCompleted()).isFalse();
    }

    @Test
    void isCompleted_whenStatusIsFailed_returnsFalse() {
        Load load = Load.builder().status(LoadStatus.FAILED).build();
        assertThat(load.isCompleted()).isFalse();
    }

    @Test
    void addAttacheFile_addsFileToList() {
        Load load = Load.builder().loadName("test").build();
        AttacheFile attacheFile = AttacheFile.builder()
                .originFileName("file.csv")
                .saveFileName("uuid.csv")
                .savePath("/load_1/attache")
                .build();

        load.addAttacheFile(attacheFile);

        assertThat(load.getAttacheFiles()).hasSize(1);
        assertThat(load.getAttacheFiles().getFirst()).isSameAs(attacheFile);
    }

    @Test
    void addAttacheFile_setsLoadReferenceOnAttacheFile() {
        Load load = Load.builder().loadName("test").build();
        AttacheFile attacheFile = AttacheFile.builder()
                .originFileName("file.csv")
                .saveFileName("uuid.csv")
                .savePath("/load_1/attache")
                .build();

        load.addAttacheFile(attacheFile);

        assertThat(attacheFile.getLoad()).isSameAs(load);
    }
}