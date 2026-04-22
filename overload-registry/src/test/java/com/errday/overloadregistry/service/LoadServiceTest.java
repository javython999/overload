package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.SliceResponse;
import com.errday.overloadregistry.dto.load.LoadListResponse;
import com.errday.overloadregistry.dto.load.LoadRegisterRequest;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.enums.LoadStatus;
import com.errday.overloadregistry.repository.LoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @InjectMocks
    private LoadService loadService;

    @Test
    void save_createsLoadWithRegisteredStatus() {
        LoadRegisterRequest request = new LoadRegisterRequest("my-load", null, List.of());
        Load saved = Load.builder().id(1L).loadName("my-load").status(LoadStatus.REGISTERED).build();
        given(loadRepository.save(any(Load.class))).willReturn(saved);

        Load result = loadService.save(request);

        assertThat(result.getLoadName()).isEqualTo("my-load");
        assertThat(result.getStatus()).isEqualTo(LoadStatus.REGISTERED);
        verify(loadRepository).save(any(Load.class));
    }

    @Test
    void findById_returnsLoad_whenExists() {
        Load load = Load.builder().id(1L).loadName("test").build();
        given(loadRepository.findById(1L)).willReturn(Optional.of(load));

        Load result = loadService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsIllegalArgumentException_whenNotFound() {
        given(loadRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loadService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Load not found: 99");
    }

    @Test
    void paging_returnsSliceResponseWithContent() {
        Load load = Load.builder()
                .id(1L)
                .loadName("test")
                .status(LoadStatus.REGISTERED)
                .createAt(LocalDateTime.now())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        given(loadRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(load), pageable, 1));

        SliceResponse<LoadListResponse> response = loadService.paging(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).loadName()).isEqualTo("test");
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void updateStatus_updatesLoadStatus() {
        Load load = Load.builder().id(1L).status(LoadStatus.REGISTERED).build();
        given(loadRepository.findById(1L)).willReturn(Optional.of(load));

        loadService.updateStatus(1L, LoadStatus.RUNNING);

        assertThat(load.getStatus()).isEqualTo(LoadStatus.RUNNING);
    }

    @Test
    void updateStatus_throwsIllegalArgumentException_whenLoadNotFound() {
        given(loadRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loadService.updateStatus(99L, LoadStatus.RUNNING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Load not found: 99");
    }
}