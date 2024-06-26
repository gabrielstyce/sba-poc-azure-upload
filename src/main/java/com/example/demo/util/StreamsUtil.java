package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.*;

@Slf4j
public class StreamsUtil {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static InputStream pipefy(InputStream inputStream) throws IOException {
        return pipefy(inputStream, DEFAULT_BUFFER_SIZE);
    }

    public static InputStream pipefy(InputStream inputStream, final int buffer) throws IOException {
        final PipedInputStream pipedInputStream = new PipedInputStream(buffer);
        final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);

        log.debug("Piping with buffer - {}", buffer);
        pipe(inputStream, pipedOutputStream, buffer);

        return pipedInputStream;
    }

    public static void pipe(InputStream inputStream, PipedOutputStream outputStream) {
        pipe(inputStream, outputStream, DEFAULT_BUFFER_SIZE);
    }

    public static void pipe(InputStream inputStream, PipedOutputStream outputStream, final int bufferSize) {
        try (final SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor()) {
            taskExecutor.execute(() -> {
                try {
                    write(inputStream, outputStream, bufferSize);
                } catch (IOException e) {
                    log.error("Error while writing stream - pipe-write", e);
                    throw new RuntimeException(e);
                } finally {
                    safeClose(inputStream);
                    safeClose(outputStream);
                    safeClose(taskExecutor);
                }
            });
        }
    }

    private static void write(InputStream var0, PipedOutputStream var1, final int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];

        int byteData;
        while ((byteData = var0.read(buffer, 0, buffer.length)) >= 0) {
            var1.write(buffer, 0, byteData);
        }
    }

    private static void safeClose(AutoCloseable  c) {
        try {
            c.close();
        } catch (Exception ex) {
            log.error("Error while closing stream - pipe-write", ex);
            throw new RuntimeException(ex);
        }
    }
}
