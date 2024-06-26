package com.example.demo.service;

import com.azure.core.util.ProgressListener;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import com.example.demo.configuration.AzureBlobStorageConfiguration;
import com.example.demo.listener.LogProgressListener;
import com.example.demo.model.record.BlobUploadInfo;
import com.example.demo.model.record.DownloadResourceInfo;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@ToString
public class AzureBlobService {

    @Autowired
    private AzureBlobStorageConfiguration storage;

    public BlobUploadInfo createEmptyFile(long sizeInBytes) {
        final String fileName = UUID.randomUUID() + ".txt";
        final BlobClient blobClient = storage.getBlobClient(fileName);

        byte[] emptyContent = new byte[(int) sizeInBytes];
        blobClient.upload(new ByteArrayInputStream(emptyContent), sizeInBytes, true);

        final String tokenSaS = generateTokenSas(fileName);

        return new BlobUploadInfo(fileName, null, blobClient.getBlobUrl(), tokenSaS, null);
    }

    public BlobUploadInfo uploadFile(DownloadResourceInfo downloadResourceInfo) throws FileNotFoundException {
        final ProgressListener uploadProgressLogger = LogProgressListener.builder()
                .fileLength(downloadResourceInfo.size())
                .fileName(downloadResourceInfo.name())
                .build();

        // return uploadFileDirect(downloadResourceInfo);
        return uploadWithResponse(downloadResourceInfo, uploadProgressLogger);
    }

    public BlobUploadInfo uploadFileDirect(DownloadResourceInfo downloadResourceInfo) throws FileNotFoundException {
        final BlobClient blobClient = storage.getBlobClient(downloadResourceInfo.name());

        blobClient.upload(downloadResourceInfo.resource());

        return new BlobUploadInfo(downloadResourceInfo.name(), blobClient.getBlobUrl(), null, null, null);
    }

    public BlobUploadInfo uploadWithResponse(DownloadResourceInfo downloadResourceInfo, ProgressListener listener) throws FileNotFoundException {
        final BlobClient blobClient = storage.getBlobClient(downloadResourceInfo.name());

        log.info("Azure upload High Water Mark: {}", downloadResourceInfo.highWaterMark());

        final ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions()
                .setMaxConcurrency(1)
                .setBlockSizeLong(downloadResourceInfo.highWaterMark())
                .setProgressListener(listener);

        final BlobParallelUploadOptions blobParallelUploadOptions = new BlobParallelUploadOptions(downloadResourceInfo.resource());
        blobParallelUploadOptions.setParallelTransferOptions(parallelTransferOptions);
        blobParallelUploadOptions.setComputeMd5(Boolean.TRUE);

        blobClient.uploadWithResponse(blobParallelUploadOptions, null, null);

        return new BlobUploadInfo(downloadResourceInfo.name(), null, blobClient.getBlobUrl(), null, null);
    }

    public String generateTokenSas(String blobName) {
        final BlobClient blobClient = storage.getBlobClient(blobName);
        return generateTokenSas(blobClient);
    }

    public String generateTokenSas(BlobClient blobClient) {
        final OffsetDateTime expirationTime = OffsetDateTime.now().plusDays(1);

        final BlobContainerSasPermission permissions = new BlobContainerSasPermission()
                .setReadPermission(true)
                .setWritePermission(true);

        final BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(
                expirationTime,
                permissions
        )
                .setProtocol(SasProtocol.HTTPS_ONLY);

        final String blobUrl = blobClient.getBlobUrl();
        final String tokenSas = blobClient.generateSas(sasSignatureValues);

        log.debug("File URL: {}", blobUrl);
        log.debug("Token Sas: {}", tokenSas);
        return String.format("%s?%s", blobUrl, tokenSas);
    }
}
