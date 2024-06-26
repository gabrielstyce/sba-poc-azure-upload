package com.example.demo.client;

import com.example.demo.constants.Constants;
import com.example.demo.extractor.BufferInputStreamResponseExtractor;
import com.example.demo.extractor.InputStreamResponseExtractor;
import com.example.demo.extractor.TempFileResponseExtractor;
import com.example.demo.factory.FileNameFactory;
import com.example.demo.factory.UUidFileNameFactory;
import com.example.demo.model.record.DownloadResourceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTestClient {

    private final RestTemplate restTemplate;

    private static RequestCallback getOctetStreamRequestCallback() {
        final List<MediaType> acceptedHeaders = Arrays.asList(
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.ALL
        );
        return request -> request.getHeaders().setAccept(acceptedHeaders);
    }

    public DownloadResourceInfo downloadImageStreamingResponse(String alias, String url, Consumer<DownloadResourceInfo> streamConsumer) {
        final RequestCallback requestCallback = getOctetStreamRequestCallback();

        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        final ResponseExtractor<DownloadResourceInfo> responseExtractor = InputStreamResponseExtractor.builder()
                .fileNameFactory(fileNameFactory)
                .streamConsumer(streamConsumer)
                .build();

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public DownloadResourceInfo downloadImageBufferingStreamingResponseHWM4KB(String alias, String url, Consumer<DownloadResourceInfo> streamConsumer) {
        final RequestCallback requestCallback = getOctetStreamRequestCallback();

        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        log.debug("Download file streaming with high-water-mark of 4kb");
        final ResponseExtractor<DownloadResourceInfo> responseExtractor = BufferInputStreamResponseExtractor.builder()
                .fileNameFactory(fileNameFactory)
                .streamConsumer(streamConsumer)
                .highWaterMark(Constants.HWM4KB)
                .build();

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public DownloadResourceInfo downloadImageBufferingStreamingResponseDefault(String alias, String url, Consumer<DownloadResourceInfo> streamConsumer) {
        final RequestCallback requestCallback = getOctetStreamRequestCallback();

        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        log.debug("Download file streaming with default azure high-water-mark (4mb)");
        final ResponseExtractor<DownloadResourceInfo> responseExtractor = BufferInputStreamResponseExtractor.builder()
                .fileNameFactory(fileNameFactory)
                .streamConsumer(streamConsumer)
                .build();

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public DownloadResourceInfo downloadImageBufferingStreamingResponseHWM8MB(String alias, String url, Consumer<DownloadResourceInfo> streamConsumer) {
        final RequestCallback requestCallback = getOctetStreamRequestCallback();

        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        log.debug("Download file streaming with high-water-mark of 8mb");
        final ResponseExtractor<DownloadResourceInfo> responseExtractor = BufferInputStreamResponseExtractor.builder()
                .fileNameFactory(fileNameFactory)
                .streamConsumer(streamConsumer)
                .highWaterMark(Constants.HWM8MB)
                .build();

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public DownloadResourceInfo downloadImageBufferingResponseInTempFile(String alias, String url) {
        final RequestCallback requestCallback = getOctetStreamRequestCallback();

        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        log.debug("download file file with high-water-mark of 4mb");
        final ResponseExtractor<DownloadResourceInfo> responseExtractor = TempFileResponseExtractor.builder()
                .fileNameFactory(fileNameFactory)
                .build();

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public DownloadResourceInfo downloadImageBufferingAllResponseInMemory(String alias, String url) throws IOException {
        final FileNameFactory fileNameFactory = UUidFileNameFactory.builder()
                .prefix(alias)
                .suffix(".txt")
                .build();

        try {
            final ResponseEntity<Resource> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Resource.class
            );

            final var responseBody = responseEntity.getBody();
            Assert.notNull(responseBody, "No body data was provided!");

            return new DownloadResourceInfo(
                    fileNameFactory.get(null),
                    null,
                    responseEntity.getHeaders().getContentLength(),
                    null,
                    responseBody.getInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

