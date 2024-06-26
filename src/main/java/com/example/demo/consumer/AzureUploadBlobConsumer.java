package com.example.demo.consumer;

import com.example.demo.model.record.BlobUploadInfo;
import com.example.demo.model.record.DownloadResourceInfo;
import com.example.demo.service.AzureBlobService;
import com.example.demo.util.PerformanceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Builder
@AllArgsConstructor
public class AzureUploadBlobConsumer implements Consumer<DownloadResourceInfo> {

    private String alias;
    private AzureBlobService azureBlobService;
    private CompletableFuture<BlobUploadInfo> handler;

    @Override
    public void accept(DownloadResourceInfo downloadResourceInfo) {
        try {
            final BlobUploadInfo blobUploadInfo = azureBlobService.uploadFile(downloadResourceInfo);
            log.debug("{} took {} ", alias,PerformanceUtil.compare(alias));

            if (!ObjectUtils.isEmpty(handler)) {
                final var uploadDuration = PerformanceUtil.end(alias);
                blobUploadInfo.setTag(alias);
                blobUploadInfo.setUploadDuration(uploadDuration);
                handler.complete(blobUploadInfo);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
