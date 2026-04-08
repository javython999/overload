package com.errday.overloadregistry.service;

import com.errday.overloadregistry.dto.SliceResponse;
import com.errday.overloadregistry.dto.load.LoadListResponse;
import com.errday.overloadregistry.dto.load.LoadRegisterRequest;
import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.enums.LoadStatus;
import com.errday.overloadregistry.repository.LoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;

    public Load save(LoadRegisterRequest request) {
        Load load = Load.builder()
                .loadName(request.loadName())
                .status(LoadStatus.REGISTERED)
                .build();

        return loadRepository.save(load);
    }

    @Transactional(readOnly = true)
    public Load findById(Long id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + id));
    }

    @Transactional(readOnly = true)
    public SliceResponse<LoadListResponse> paging(Pageable pageable) {
        Slice<LoadListResponse> slice = loadRepository.findAll(pageable)
                .map(LoadListResponse::new);

        return new SliceResponse<>(
                slice.getContent(),
                slice.hasNext()
        );
    }

    public void updateStatus(Long loadId, LoadStatus status) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
        load.setStatus(status);
        log.info("Updated load status: loadId={}, status={}", loadId, status);
    }

}
