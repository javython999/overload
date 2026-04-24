package com.errday.overloadregistry.dto.load;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoadRegisterRequestTest {

    @Test
    void hasAttacheFiles_returnsFalse_whenAnyFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        LoadRegisterRequest request = new LoadRegisterRequest("test", null, List.of(emptyFile));

        assertThat(request.hasAttacheFiles()).isFalse();
    }

    @Test
    void hasAttacheFiles_returnsTrue_whenAllFilesAreNonEmpty() {
        MultipartFile nonEmptyFile = new MockMultipartFile("file", "data.csv", "text/plain", "content".getBytes());
        LoadRegisterRequest request = new LoadRegisterRequest("test", null, List.of(nonEmptyFile));

        assertThat(request.hasAttacheFiles()).isTrue();
    }

    @Test
    void hasAttacheFiles_returnsFalse_whenListIsEmpty() {
        LoadRegisterRequest request = new LoadRegisterRequest("test", null, List.of());

        assertThat(request.hasAttacheFiles()).isFalse();
    }

    @Test
    void hasAttacheFiles_returnsFalse_whenNull() {
        LoadRegisterRequest request = new LoadRegisterRequest("test", null, null);

        assertThat(request.hasAttacheFiles()).isFalse();
    }

    @Test
    void hasAttacheFiles_returnsTrue_whenMixedFiles() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        MultipartFile nonEmptyFile = new MockMultipartFile("file", "data.csv", "text/plain", "content".getBytes());
        LoadRegisterRequest request = new LoadRegisterRequest("test", null, List.of(emptyFile, nonEmptyFile));

        assertThat(request.hasAttacheFiles()).isTrue();
    }
}