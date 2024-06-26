package com.example.demo.constants;

import com.azure.storage.blob.BlobClient;

public class Constants {
    public static final int HWM4KB = 4096;
    public static final int HWM4MB = BlobClient.BLOB_DEFAULT_UPLOAD_BLOCK_SIZE;
    public static final int HWM8MB = 8388608;
}
