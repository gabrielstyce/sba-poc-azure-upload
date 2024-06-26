package com.example.demo.util;

import com.example.demo.model.record.BlobUploadInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableFutureUtil {

    public static CompletableFuture<List<String>> composeStringResponse(List<CompletableFuture<BlobUploadInfo>> futures) {
        final CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

        return allOfFuture.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .map(r -> r.getTag() + " | " + r.getUploadDuration())
                .collect(Collectors.toList()));
    }

}
