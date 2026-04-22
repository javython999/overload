package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.attachefile.AttacheFileSaveResponse;
import com.errday.overloadregistry.entity.AttacheFile;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.repository.AttacheFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttacheFileServiceTest {

    @Mock
    private FileService fileService;

    @Mock
    private AttacheFileRepository attacheFileRepository;

    @InjectMocks
    private AttacheFileService attacheFileService;

    @Test
    void save_savesAllFilesAndAddsToLoad() throws IOException {
        Load load = Load.builder().id(1L).loadName("test").build();
        MultipartFile file1 = new MockMultipartFile("f1", "a.csv", "text/plain", "data".getBytes());
        MultipartFile file2 = new MockMultipartFile("f2", "b.csv", "text/plain", "data".getBytes());

        given(fileService.saveAttacheFile(load, file1))
                .willReturn(new AttacheFileSaveResponse("a.csv", "uuid1.csv", "/load_1/attache"));
        given(fileService.saveAttacheFile(load, file2))
                .willReturn(new AttacheFileSaveResponse("b.csv", "uuid2.csv", "/load_1/attache"));

        List<AttacheFile> result = attacheFileService.save(load, List.of(file1, file2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOriginFileName()).isEqualTo("a.csv");
        assertThat(result.get(1).getOriginFileName()).isEqualTo("b.csv");
        assertThat(load.getAttacheFiles()).hasSize(2);
        assertThat(result.get(0).getLoad()).isSameAs(load);
    }

    @Test
    void findByLoadId_returnsAttacheFiles() {
        List<AttacheFile> files = List.of(
                AttacheFile.builder().id(1L).originFileName("a.csv").build(),
                AttacheFile.builder().id(2L).originFileName("b.csv").build()
        );
        given(attacheFileRepository.findByLoadId(5L)).willReturn(files);

        List<AttacheFile> result = attacheFileService.findByLoadId(5L);

        assertThat(result).hasSize(2);
        verify(attacheFileRepository).findByLoadId(5L);
    }
}