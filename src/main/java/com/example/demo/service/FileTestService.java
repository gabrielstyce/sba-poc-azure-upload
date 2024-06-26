package com.example.demo.service;

import com.example.demo.client.FileTestClient;
import com.example.demo.configuration.AzureBlobStorageConfiguration;
import com.example.demo.consumer.AzureUploadBlobConsumer;
import com.example.demo.model.record.BlobUploadInfo;
import com.example.demo.model.record.DownloadResourceInfo;
import com.example.demo.util.CompletableFutureUtil;
import com.example.demo.util.PerformanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class FileTestService {

    @Autowired
    private Executor asyncExecutor;

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private FileTestClient fileTestClient;

    @Autowired
    private AzureBlobStorageConfiguration azureBlobStorageConfiguration;

    public String createEmptyFile(long sizeInMb) {
        long sizeInBytes = sizeInMb * 1024 * 1024; // Transform MB to Bytes
        return azureBlobService.createEmptyFile(sizeInBytes).toString();
    }

    public String getFileName(String endpoint, String tokenSas) {
        final var blobClient = azureBlobStorageConfiguration.getBlobClientBySaS(endpoint, tokenSas);
        return blobClient.getBlobName();
    }

    public CompletableFuture<BlobUploadInfo> downloadImageStreamingResponse(String resourceURI, String ar) {
        final String ALIAS = "downloadImageStreamingResponse" + ar;
        final CompletableFuture<BlobUploadInfo> finalFuture = new CompletableFuture<>();

        return CompletableFuture.supplyAsync(() -> {
            final var uploadConsumer = AzureUploadBlobConsumer.builder()
                    .azureBlobService(azureBlobService)
                    .handler(finalFuture)
                    .alias(ALIAS)
                    .build();

            PerformanceUtil.create(ALIAS);
            fileTestClient.downloadImageStreamingResponse(ALIAS, resourceURI, uploadConsumer);

            return finalFuture.join();
        }, asyncExecutor);
    }

    public CompletableFuture<BlobUploadInfo> downloadImageBufferingResponseInTempFile(String resourceURI, String ar) {
        final String ALIAS = "downloadImageBufferingResponseInTempFile" + ar;

        return CompletableFuture.supplyAsync(() -> {
            try {
                PerformanceUtil.create(ALIAS);
                final DownloadResourceInfo response = fileTestClient.downloadImageBufferingResponseInTempFile(ALIAS, resourceURI);
                final BlobUploadInfo blobUploadInfo = azureBlobService.uploadFile(response);
                blobUploadInfo.setUploadDuration(PerformanceUtil.end(ALIAS));
                return blobUploadInfo;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, asyncExecutor);
    }

    public CompletableFuture<BlobUploadInfo> downloadImageBufferingAllResponseInMemory(String resourceURI, String ar) {
        final String ALIAS = "downloadImageBufferingAllResponseInMemory" + ar;

        return CompletableFuture.supplyAsync(() -> {
            try {
                PerformanceUtil.create(ALIAS);
                final DownloadResourceInfo response = fileTestClient.downloadImageBufferingAllResponseInMemory(ALIAS, resourceURI);
                final BlobUploadInfo blobUploadInfo = azureBlobService.uploadFile(response);
                blobUploadInfo.setUploadDuration(PerformanceUtil.end(ALIAS));
                return blobUploadInfo;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, asyncExecutor);
    }

    public CompletableFuture<BlobUploadInfo> downloadImageBufferingStreamingResponse4096(String resourceURI, String ar) {
        final String ALIAS = "downloadImageBufferingStreamingResponse4096" + ar;
        final CompletableFuture<BlobUploadInfo> finalFuture = new CompletableFuture<>();

        return CompletableFuture.supplyAsync(() -> {
            final var uploadConsumer = AzureUploadBlobConsumer.builder()
                    .azureBlobService(azureBlobService)
                    .handler(finalFuture)
                    .alias(ALIAS)
                    .build();

            PerformanceUtil.create(ALIAS);
            fileTestClient.downloadImageBufferingStreamingResponseHWM4KB(ALIAS, resourceURI, uploadConsumer);

            return finalFuture.join();
        }, asyncExecutor);
    }

    public CompletableFuture<BlobUploadInfo> downloadImageBufferingStreamingResponseDefault(String resourceURI, String ar) {
        final String ALIAS = "downloadImageBufferingStreamingResponseDefault" + ar;
        final CompletableFuture<BlobUploadInfo> finalFuture = new CompletableFuture<>();

        return CompletableFuture.supplyAsync(() -> {
            final var uploadConsumer = AzureUploadBlobConsumer.builder()
                    .azureBlobService(azureBlobService)
                    .handler(finalFuture)
                    .alias(ALIAS)
                    .build();

            PerformanceUtil.create(ALIAS);
            fileTestClient.downloadImageBufferingStreamingResponseDefault(ALIAS, resourceURI, uploadConsumer);

            return finalFuture.join();
        }, asyncExecutor);
    }

    public CompletableFuture<BlobUploadInfo> downloadImageBufferingStreamingResponse8388608(String resourceURI, String ar) {
        final String ALIAS = "downloadImageBufferingStreamingResponse8388608" + ar;
        final CompletableFuture<BlobUploadInfo> finalFuture = new CompletableFuture<>();

        return CompletableFuture.supplyAsync(() -> {
            final var uploadConsumer = AzureUploadBlobConsumer.builder()
                    .azureBlobService(azureBlobService)
                    .handler(finalFuture)
                    .alias(ALIAS)
                    .build();

            PerformanceUtil.create(ALIAS);
            fileTestClient.downloadImageBufferingStreamingResponseHWM8MB(ALIAS, resourceURI, uploadConsumer);

            return finalFuture.join();
        }, asyncExecutor);
    }

    public CompletableFuture<List<String>> doRun(String url, String command) {
        final List<CompletableFuture<BlobUploadInfo>> lista = new ArrayList<>();
        for (int i = 0; i < 125; i++) {
            switch (command) {
                case "1":
                    lista.add(downloadImageBufferingStreamingResponseDefault(url, String.valueOf(i)));
                    break;
                case "2":
                    lista.add(downloadImageBufferingStreamingResponse4096(url, String.valueOf(i)));
                    break;
                case "3":
                    lista.add(downloadImageBufferingStreamingResponse8388608(url, String.valueOf(i)));
                    break;
                case "4":
                    lista.add(downloadImageBufferingAllResponseInMemory(url, String.valueOf(i)));
                    break;
                case "5":
                    lista.add(downloadImageBufferingResponseInTempFile(url, String.valueOf(i)));
                    break;
                case "6":
                    lista.add(downloadImageStreamingResponse(url, String.valueOf(i)));
                    break;
                default:
                    break;
            }
        }

        return CompletableFutureUtil.composeStringResponse(lista);
    }
}
