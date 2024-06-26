package com.example.demo.factory;

import jakarta.annotation.Nullable;
import org.springframework.http.client.ClientHttpResponse;

public interface FileNameFactory {
    String get(
            @Nullable
            ClientHttpResponse response
    );
}
