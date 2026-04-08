package com.errday.overloadworker.service;

import com.errday.overloadworker.LoadStatus;
import com.errday.overloadworker.dto.KafkaConsumeDto;
import com.errday.overloadworker.dto.LoadStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadConsumerService {

    private final DownloadService downloadService;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final KafkaListenerEndpointRegistry registry;

    @Value("${script.log-path}")
    private String logPath;

    @Value("${prometheus.endpoints}")
    private String prometheusEndpoints;

    @Value("${script.bundle-script}")
    private String bundleScript;

    @KafkaListener(
            id = "load-test-listener",
            topics = "load-test",
            groupId = "overload-worker-group",
            concurrency = "1"
    )
    public void consume(String message) {
        log.info("Received message: {}", message);

        File loadDir = null;
        try {

            KafkaConsumeDto kafkaConsumeDto = objectMapper.readValue(message, KafkaConsumeDto.class);
            Long loadId = kafkaConsumeDto.getLoadId();
            log.info("Processing start for loadId: {}", loadId);

            kafkaProducerService.sendStatus(new LoadStatusDto(loadId, LoadStatus.RUNNING));

            loadDir = downloadService.downloadAndUnzipLoadFiles(loadId);

            // 2. 스크립트 파일 찾기 및 k6 run 실행
            if (kafkaConsumeDto.getScriptFileName() != null) {
                File scriptFile = new File(loadDir, kafkaConsumeDto.getScriptFileName());
                if (scriptFile.exists()) {

                    runK6Script(scriptFile, initLogFile(loadId));
                    
                    File summaryFile = new File(loadDir, "summary.json");
                    if (summaryFile.exists()) {
                        String summaryJson = Files.readString(summaryFile.toPath());
                        log.info(summaryJson);
                        kafkaProducerService.sendResult(loadId, summaryJson);
                    }
                    kafkaProducerService.sendStatus(new LoadStatusDto(loadId, LoadStatus.COMPLETED));
                } else {
                    log.error("Script file not found: {}", scriptFile.getAbsolutePath());
                    kafkaProducerService.sendStatus(new LoadStatusDto(loadId, LoadStatus.FAILED));
                }
            }

            log.info("Processing end for loadId: {}", loadId);

        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
            try {
                KafkaConsumeDto kafkaConsumeDto = objectMapper.readValue(message, KafkaConsumeDto.class);
                kafkaProducerService.sendStatus(new LoadStatusDto(kafkaConsumeDto.getLoadId(), LoadStatus.FAILED));
            } catch (JsonProcessingException jpe) {
                log.error("JsonProcessingException: {}", message, jpe);
            }
        } finally {
           if (loadDir != null && loadDir.exists()) {
                try {
                    log.info("Deleting load directory: {}", loadDir.getAbsolutePath());
                    FileUtils.deleteDirectory(loadDir);
                } catch (IOException e) {
                    log.warn("Failed to delete directory: {}", loadDir.getAbsolutePath(), e);
                }
           }
            log.info("Listening for load test messages");
        }
    }

    private void runK6Script(File scriptFile, File logFile) {
        try {
            File currentDirectory = scriptFile.getParentFile();
            File wrapperScript = wrapperScriptFile(scriptFile);

            String[] command = {
                    "k6",
                    "run",
                    "--insecure-skip-tls-verify",
                    "--out",
                    "experimental-prometheus-rw",
                    wrapperScript.getAbsolutePath()
            };
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(currentDirectory);

            Map<String, String> env = processBuilder.environment();
            env.put("K6_PROMETHEUS_RW_SERVER_URL", prometheusEndpoints);
            env.put("K6_PROMETHEUS_RW_PUSH_INTERVAL", "1s");
            env.put("K6_PROMETHEUS_RW_STALE_MARKER_INTERVAL", "1s");

            processBuilder.redirectOutput(logFile);
            processBuilder.redirectErrorStream(true);

            log.info("Executing command: {}", String.join(" ", command)); // 실행 명령 확인용 로그

            Process process = processBuilder.start();
            log.info("Started k6 process for script: {}", scriptFile.getName());

            int exitCode = process.waitFor();
            log.info("k6 process finished with exit code: {} for script: {}", exitCode, scriptFile.getName());

        } catch (IOException | InterruptedException e) {
            log.error("Error while running k6 script: {}", scriptFile.getName(), e);
        }
    }

    private File initLogFile(long loadId) {
        String logFilePath = logPath + "/load_" + loadId;

        File loadDir = new File(logFilePath);
        if (!loadDir.exists() && !loadDir.mkdirs()) {
            log.error("Failed to create directory: {}", logFilePath);
        }

        return new File(logFilePath,  "load.log");
    }

    private File wrapperScriptFile(File originScript) throws IOException {
        File wrapper = new File(originScript.getParent(), "wrapper.js");
        Files.writeString(wrapper.toPath(), wrapperScriptContent(originScript));
        return wrapper;
    }

    private String wrapperScriptContent(File originScript) {
        String bundleScriptPath = formatFileUrl(bundleScript);
        String absolutePath = formatFileUrl(originScript.getAbsolutePath());


        return """
                import { htmlReport } from '%s';
                import * as original from '%s';

                export const options = original.options;

                export default function () {
                    return original.default();
                }

                function getMetric(data, name) {
                    return (data.metrics[name] && data.metrics[name].values) ? data.metrics[name].values : {};
                }

                function buildTrends(data) {
                    const metricNames = [
                        "http_req_blocked",
                        "http_req_connecting",
                        "http_req_duration",
                        "http_req_receiving",
                        "http_req_sending",
                        "http_req_tls_handshaking",
                        "http_req_waiting",
                        "iteration_duration",
                    ];

                    return metricNames.map((name) => {
                        const v = getMetric(data, name);
                        return {
                            metric: name,
                            avg: v.avg,
                            min: v.min,
                            med: v.med,
                            max: v.max,
                            p90: v["p(90)"],
                            p95: v["p(95)"],
                        };
                    });
                }

                function buildRates(data) {
                    const f = getMetric(data, "checks");
                    return {
                        rate: f.rate,
                        passes: f.passes,
                        fails: f.fails,
                    };
                }

                function buildOverview(data) {
                    return {
                        iterations: {
                            total: data.metrics.iterations.values.count,
                            rate: data.metrics.iterations.values.rate,
                        },
                        virtual_users: {
                            min: data.metrics.vus.values.min,
                            max: data.metrics.vus.values.max,
                        },
                        requests: {
                            total: data.metrics.http_reqs.values.count,
                            rate: data.metrics.http_reqs.values.rate,
                        },
                        data_received: {
                            total: data.metrics.data_received.values.count,
                            rate: data.metrics.data_received.values.rate,
                        },
                        data_sent: {
                            total: data.metrics.data_sent.values.count,
                            rate: data.metrics.data_sent.values.rate,
                        },
                    };
                }

                export function handleSummary(data) {
                    const result = {
                        overview: buildOverview(data),
                        trends: buildTrends(data),
                        rates: buildRates(data),
                    };
                    return {
                        "summary.json": JSON.stringify(result, null, 2),
                        "summary.html": htmlReport(data),
                    };
                }
                """.formatted(bundleScriptPath, absolutePath);
    }

    private String formatFileUrl(String path) {
        String formatted = path.replace("\\", "/");
        // 윈도우 절대 경로(C:/...) 대응: 앞에 /가 없다면 추가
        if (!formatted.startsWith("/")) {
            formatted = "/" + formatted;
        }
        return "file://" + formatted;
    }
}
