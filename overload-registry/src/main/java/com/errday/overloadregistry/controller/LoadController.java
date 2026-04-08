package com.errday.overloadregistry.controller;

import com.errday.overloadregistry.dto.load.LoadRegisterRequest;
import com.errday.overloadregistry.service.LoadRegisterOrchestration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/loads")
@RequiredArgsConstructor
public class LoadController {

    private final LoadRegisterOrchestration loadRegisterOrchestration;
    private final String viewFolder = "load";

    @GetMapping()
    public String write() {
        return viewFolder + "/write";
    }

    @PostMapping()
    public String post(LoadRegisterRequest request) throws IOException {
        long loadId = loadRegisterOrchestration.save(request);
        return "redirect:/loads/" + loadId;
    }

    @GetMapping("/{loadId}")
    public String read(@PathVariable long loadId, Model model) {
        model.addAttribute("loadId", loadId);
        return viewFolder + "/read";
    }

    @GetMapping("/test")
    public String test() {
        return viewFolder + "/test";
    }
}
