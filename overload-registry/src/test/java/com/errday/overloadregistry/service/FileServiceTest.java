package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.attachefile.AttacheFileSaveResponse;
import com.errday.overloadregistry.dto.load.script.ScriptFileSaveResponse;
import com.errday.overloadregistry.entity.Load;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {

    @TempDir
    Path tempDir;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
    }

    @Test
    void saveScriptFile_savesFileAndReturnsMetadata() throws IOException {
        Load load = Load.builder().id(1L).build();
        MockMultipartFile scriptFile = new MockMultipartFile(
                "script", "test.js", "text/plain", "k6 script content".getBytes());

        ScriptFileSaveResponse response = fileService.saveScriptFile(load, scriptFile);

        assertThat(response.originFileName()).isEqualTo("test.js");
        assertThat(response.saveFileName()).endsWith(".js");
        assertThat(response.savePath()).isEqualTo("/load_1/script");

        Path savedFile = tempDir.resolve("load_1/script").resolve(response.saveFileName());
        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.readString(savedFile)).isEqualTo("k6 script content");
    }

    @Test
    void saveAttacheFile_savesFileAndReturnsMetadata() throws IOException {
        Load load = Load.builder().id(2L).build();
        MockMultipartFile attacheFile = new MockMultipartFile(
                "file", "data.csv", "text/csv", "col1,col2\nv1,v2".getBytes());

        AttacheFileSaveResponse response = fileService.saveAttacheFile(load, attacheFile);

        assertThat(response.originFileName()).isEqualTo("data.csv");
        assertThat(response.saveFileName()).endsWith(".csv");
        assertThat(response.savePath()).isEqualTo("/load_2/attache");

        Path savedFile = tempDir.resolve("load_2/attache").resolve(response.saveFileName());
        assertThat(Files.exists(savedFile)).isTrue();
    }

    @Test
    void readScriptFile_readsFileContent() throws IOException {
        Path scriptDir = tempDir.resolve("load_1/script");
        Files.createDirectories(scriptDir);
        Path scriptFile = scriptDir.resolve("test.js");
        Files.writeString(scriptFile, "import http from 'k6/http';");

        String content = fileService.readScriptFile("/load_1/script/test.js");

        assertThat(content).isEqualTo("import http from 'k6/http';");
    }

    @Test
    void getPath_returnsCorrectPath() {
        Path result = fileService.getPath("/load_1/script/uuid.js");

        assertThat(result).isEqualTo(tempDir.resolve("load_1/script/uuid.js"));
    }
}