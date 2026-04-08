package com.errday.overloadworker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadService {

    private final RestTemplate restTemplate;

    @Value("${script.download-path}")
    private String downloadPath;

    @Value("${script.registry-url}")
    private String registryUrl;

    public File downloadAndUnzipLoadFiles(Long loadId) throws IOException {
        String url = registryUrl + "/load/" + loadId;
        byte[] zipData = restTemplate.getForObject(url, byte[].class);

        if (zipData == null) {
            throw new IOException("Failed to download zip from: " + url);
        }

        String loadDirPath = downloadPath + "/load_" + loadId;
        File loadDir = new File(loadDirPath);
        if (!loadDir.exists() && !loadDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + loadDirPath);
        }

        File zipFile = new File(loadDirPath, "load_" + loadId + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(zipData);
        }

        unzip(zipFile, loadDir);
        
        log.info("Downloaded and unzipped load files to: {}", loadDir.getAbsolutePath());
        return loadDir;
    }

    private void unzip(File zipFile, File destDir) throws IOException {
        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFile))) {
            java.util.zip.ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    private File newFile(File destinationDir, java.util.zip.ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
