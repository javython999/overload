package com.errday.overloadregistry.controller;

import com.errday.overloadregistry.service.DownloadOrchestration;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {

    private final DownloadOrchestration downloadOrchestration;

    @GetMapping("/{saveFileName}")
    public ResponseEntity<Resource> download(@PathVariable String saveFileName) throws IOException {
        Resource resource = downloadOrchestration.downloadFile(saveFileName);
        String originFileName = downloadOrchestration.getOriginFileName(saveFileName);

        String encodedFileName = UriUtils.encode(originFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/load/{loadId}")
    public ResponseEntity<Resource> downloadLoadFiles(@PathVariable Long loadId) throws IOException {
        Resource resource = downloadOrchestration.downloadZip(loadId);
        String zipFileName = "load_" + loadId + ".zip";
        String encodedFileName = UriUtils.encode(zipFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
