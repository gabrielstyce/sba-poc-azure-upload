package com.example.demo.extractor;

import com.example.demo.factory.FileNameFactory;
import com.example.demo.model.record.DownloadResourceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseExtractor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Builder
@AllArgsConstructor
public class TempFileResponseExtractor implements ResponseExtractor<DownloadResourceInfo> {
    private static final String TEMP_DIR = "tmp/";
    private FileNameFactory fileNameFactory;

    @Override
    public DownloadResourceInfo extractData(@NonNull ClientHttpResponse response) throws IOException {
        Assert.notNull(fileNameFactory, "FileName factory must be provided!");

        final String fileName = fileNameFactory.get(response);
        final Path filePath = Paths.get(TEMP_DIR, fileName);

        // Deletes before
        Files.deleteIfExists(filePath);

        final OutputStream outTempFileStream = new FileOutputStream(filePath.toFile());
        final BufferedOutputStream bufferedInputStream = new BufferedOutputStream(outTempFileStream);

        StreamUtils.copy(response.getBody(), bufferedInputStream);

        return new DownloadResourceInfo(
                fileName,
                filePath,
                response.getHeaders().getContentLength(),
                null,
                Files.newInputStream(filePath)
        );
    }
}
