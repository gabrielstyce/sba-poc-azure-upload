package com.example.demo.model.record;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class BlobUploadInfo {
    private String tag;
    private String id;

    private String url;

    @Nullable
    private String token;

    @Nullable
    private Duration uploadDuration;
}
