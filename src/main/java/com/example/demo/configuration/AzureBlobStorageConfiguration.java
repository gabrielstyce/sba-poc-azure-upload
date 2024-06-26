package com.example.demo.configuration;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "demo.azure.storage")
public class AzureBlobStorageConfiguration {
    private String connectionString;
    private String containerName;

    public BlobContainerClient getBlobContainer() {
        return new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
    }

    public BlobClient getBlobClientBySaS(String endpoint, String sasToken) {
        return new BlobClientBuilder()
                .containerName(containerName)
                .endpoint(endpoint)
                .sasToken(sasToken)
                .buildClient();
    }

    public BlobClient getBlobClient(String blobName) {
        return getBlobContainer().getBlobClient(blobName);
    }

}
