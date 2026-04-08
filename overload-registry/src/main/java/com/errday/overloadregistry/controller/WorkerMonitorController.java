package com.errday.overloadregistry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/worker-monitor")
public class WorkerMonitorController {

    @GetMapping()
    public String workerMonitor() {
        return "worker-monitor/monitor";
    }
}
