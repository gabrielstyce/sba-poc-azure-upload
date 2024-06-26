package com.example.demo.controller;

import com.example.demo.service.FileTestService;
import com.example.demo.util.PerformanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Slf4j
@RestController
public class AppController {

    @Autowired
    private Executor asyncExecutor;

    @Autowired
    private FileTestService fileTestService;

    @GetMapping("/uploadEmptyFile")
    public String uploadEmptyFile(@RequestParam long sizeInMb) {
        return fileTestService.createEmptyFile(sizeInMb);
    }

    @GetMapping("/getFileName")
    public String getFileName(@RequestParam String endpoint, @RequestParam String tokenSas) {
        return fileTestService.getFileName(endpoint, tokenSas);
    }

    @GetMapping("/uploadToAzure")
//    @TimeLimiter(name = "myTimeout")
    public CompletableFuture<List<String>> uploadToAzure(@RequestParam String endpoint, @RequestParam String command) throws IOException {
        Assert.hasLength(endpoint, "No endpoint provided!");
        Assert.notNull(endpoint, "No command provided!");

        PerformanceUtil.create("createContainer");

        return fileTestService.doRun(endpoint, command).thenApplyAsync(res -> {
            res.add("createContainer | " + PerformanceUtil.end("createContainer"));
            log.info("uploadToAzure response: {}", res);
            return res;
        }, asyncExecutor);
    }

}