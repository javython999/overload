package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.load.script.ScriptFileSaveResponse;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.repository.ScriptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScriptServiceTest {

    @Mock
    private FileService fileService;

    @Mock
    private ScriptRepository scriptRepository;

    @InjectMocks
    private ScriptService scriptService;

    @Test
    void save_savesScriptWithFileInfo() throws IOException {
        Load load = Load.builder().id(1L).loadName("test").build();
        MockMultipartFile scriptFile = new MockMultipartFile("script", "test.js", "text/plain", "k6 script".getBytes());
        ScriptFileSaveResponse saveResponse = new ScriptFileSaveResponse("test.js", "uuid.js", "/load_1/script");
        Script savedScript = Script.builder()
                .id(10L)
                .originFileName("test.js")
                .saveFileName("uuid.js")
                .savePath("/load_1/script")
                .build();

        given(fileService.saveScriptFile(load, scriptFile)).willReturn(saveResponse);
        given(scriptRepository.save(any(Script.class))).willReturn(savedScript);

        Script result = scriptService.Save(load, scriptFile);

        assertThat(result.getOriginFileName()).isEqualTo("test.js");
        assertThat(result.getSaveFileName()).isEqualTo("uuid.js");
        assertThat(result.getSavePath()).isEqualTo("/load_1/script");
        verify(scriptRepository).save(any(Script.class));
    }

    @Test
    void findById_returnsScript_whenExists() {
        Script script = Script.builder().id(1L).originFileName("test.js").build();
        given(scriptRepository.findById(1L)).willReturn(Optional.of(script));

        Script result = scriptService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsIllegalArgumentException_whenNotFound() {
        given(scriptRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scriptService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Script not found: 99");
    }

    @Test
    void findByLoadId_returnsScript_whenExists() {
        Script script = Script.builder().id(1L).originFileName("test.js").build();
        given(scriptRepository.findByLoadId(5L)).willReturn(Optional.of(script));

        Script result = scriptService.findByLoadId(5L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findByLoadId_throwsIllegalArgumentException_whenNotFound() {
        given(scriptRepository.findByLoadId(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scriptService.findByLoadId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Script not found by load id : 99");
    }
}