package com.errday.overloadworker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DownloadService downloadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(downloadService, "downloadPath", tempDir.toString());
        ReflectionTestUtils.setField(downloadService, "registryUrl", "http://localhost:8080/download");
    }

    @Test
    void downloadAndUnzipLoadFiles_createsDirectoryAndExtractsFiles() throws Exception {
        byte[] zipData = createZipWithEntry("test.js", "console.log('hello');");
        given(restTemplate.getForObject(eq("http://localhost:8080/download/load/1"), eq(byte[].class)))
                .willReturn(zipData);

        File result = downloadService.downloadAndUnzipLoadFiles(1L);

        assertThat(result).exists().isDirectory();
        assertThat(new File(result, "test.js")).exists();
        assertThat(new File(result, "test.js")).content().isEqualTo("console.log('hello');");
    }

    @Test
    void downloadAndUnzipLoadFiles_throwsIOException_whenZipDataIsNull() {
        given(restTemplate.getForObject(eq("http://localhost:8080/download/load/1"), eq(byte[].class)))
                .willReturn(null);

        assertThatThrownBy(() -> downloadService.downloadAndUnzipLoadFiles(1L))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Failed to download zip");
    }

    @Test
    void downloadAndUnzipLoadFiles_extractsMultipleFiles() throws Exception {
        byte[] zipData = createZipWithEntries(
                new String[]{"script.js", "data.csv"},
                new String[]{"export default function() {}", "id,name\n1,test"}
        );
        given(restTemplate.getForObject(eq("http://localhost:8080/download/load/2"), eq(byte[].class)))
                .willReturn(zipData);

        File result = downloadService.downloadAndUnzipLoadFiles(2L);

        assertThat(new File(result, "script.js")).exists();
        assertThat(new File(result, "data.csv")).exists();
    }

    @Test
    void downloadAndUnzipLoadFiles_extractsNestedDirectories() throws Exception {
        byte[] zipData = createZipWithEntry("sub/nested.js", "nested content");
        given(restTemplate.getForObject(eq("http://localhost:8080/download/load/3"), eq(byte[].class)))
                .willReturn(zipData);

        File result = downloadService.downloadAndUnzipLoadFiles(3L);

        assertThat(new File(result, "sub/nested.js")).exists();
        assertThat(new File(result, "sub/nested.js")).content().isEqualTo("nested content");
    }

    private byte[] createZipWithEntry(String fileName, String content) throws IOException {
        return createZipWithEntries(new String[]{fileName}, new String[]{content});
    }

    private byte[] createZipWithEntries(String[] fileNames, String[] contents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < fileNames.length; i++) {
                zos.putNextEntry(new ZipEntry(fileNames[i]));
                zos.write(contents[i].getBytes());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }
}
