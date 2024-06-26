package com.example.demo.model.record;

import jakarta.annotation.Nullable;

import java.io.InputStream;
import java.nio.file.Path;

public record DownloadResourceInfo(
        String name,
        @Nullable
        Path path,
        Long size,
        Long highWaterMark,
        InputStream resource
) {
}
