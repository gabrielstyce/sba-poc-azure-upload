package com.example.demo.extractor;

import com.example.demo.factory.FileNameFactory;
import com.example.demo.model.record.DownloadResourceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.util.function.Consumer;

@Builder
@AllArgsConstructor
public class InputStreamResponseExtractor implements ResponseExtractor<DownloadResourceInfo> {

    private FileNameFactory fileNameFactory;
    private Consumer<DownloadResourceInfo> streamConsumer;

    @Override
    public DownloadResourceInfo extractData(@NonNull ClientHttpResponse response) throws IOException {
        Assert.notNull(fileNameFactory, "FileName factory must be provided!");
        Assert.notNull(streamConsumer, "Stream consumer must be provided!");

        final DownloadResourceInfo downloadResourceInfo = new DownloadResourceInfo(
                fileNameFactory.get(response),
                null,
                response.getHeaders().getContentLength(),
                null,
                response.getBody()
        );

        streamConsumer.accept(downloadResourceInfo);
        return downloadResourceInfo;
    }
}
