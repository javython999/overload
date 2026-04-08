package com.errday.overloadregistry.controller;

import com.errday.overloadregistry.dto.load.LoadDetailResponse;
import com.errday.overloadregistry.dto.load.LoadListResponse;
import com.errday.overloadregistry.dto.SliceResponse;
import com.errday.overloadregistry.service.LoadDetailOrchestration;
import com.errday.overloadregistry.service.LoadRetryOrchestration;
import com.errday.overloadregistry.service.LoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/loads")
@RequiredArgsConstructor
public class LoadRestController {

    private final LoadDetailOrchestration loadDetailOrchestration;
    private final LoadRetryOrchestration loadRetryOrchestration;
    private final LoadService loadService;

    @GetMapping()
    public SliceResponse<LoadListResponse> paging(
            @PageableDefault(
                    size = 10,
                    sort = {"createAt", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return loadService.paging(pageable);
    }

    @GetMapping("/{loadId}")
    public LoadDetailResponse loadDetail(@PathVariable Long loadId) throws IOException {
        return loadDetailOrchestration.findById(loadId);
    }


    @PostMapping("/{loadId}/retry")
    public void LoadRetry(@PathVariable Long loadId) {
        loadRetryOrchestration.retry(loadId);
    }
}
