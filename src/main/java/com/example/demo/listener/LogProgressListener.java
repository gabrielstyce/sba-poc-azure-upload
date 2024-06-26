package com.example.demo.listener;

import com.azure.core.util.ProgressListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LogProgressListener implements ProgressListener {
    private long fileLength;
    private String fileName;

    @Override
    public void handleProgress(long bytesTransferred) {
        final double percentage = ((double) bytesTransferred / fileLength) * 100;
        log.debug("File {} bytes transfered: {} of {}", fileName, bytesTransferred, fileLength);
        log.debug("File {} progress: {}%", fileName, percentage);
    }
}
