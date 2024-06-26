package com.example.demo.extractor;

import com.azure.storage.blob.BlobClient;
import com.example.demo.constants.Constants;
import com.example.demo.factory.FileNameFactory;
import com.example.demo.model.record.DownloadResourceInfo;
import com.example.demo.util.StreamsUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.ResponseExtractor;

import java.io.*;
import java.util.function.Consumer;

@Builder
@AllArgsConstructor
public class BufferInputStreamResponseExtractor implements ResponseExtractor<DownloadResourceInfo> {

    private static final Logger log = LoggerFactory.getLogger(BufferInputStreamResponseExtractor.class);
    private FileNameFactory fileNameFactory;
    private Consumer<DownloadResourceInfo> streamConsumer;
    private Integer highWaterMark;

    private int getBufferSize() {
        return ObjectUtils.isEmpty(highWaterMark) ? Constants.HWM4MB : highWaterMark;
    }

    @Override
    public DownloadResourceInfo extractData(@NonNull ClientHttpResponse response) {
        Assert.notNull(fileNameFactory, "FileName factory must be provided!");
        Assert.notNull(streamConsumer, "Stream consumer must be provided!");

        final int bufferSize = getBufferSize();

        try {
            final var pipedInputStream = StreamsUtil.pipefy(response.getBody(), bufferSize);

            final DownloadResourceInfo downloadResourceInfo = new DownloadResourceInfo(
                    fileNameFactory.get(response),
                    null,
                    response.getHeaders().getContentLength(),
                    (long) bufferSize,
                    pipedInputStream
            );

            streamConsumer.accept(downloadResourceInfo);

            return downloadResourceInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
