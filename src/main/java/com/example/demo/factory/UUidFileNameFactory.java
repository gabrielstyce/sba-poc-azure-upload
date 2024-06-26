package com.example.demo.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UUidFileNameFactory implements FileNameFactory {

    private String prefix;
    private String suffix;

    @Override
    public String get(ClientHttpResponse response) {
        return prefix + "_" + UUID.randomUUID() + "_" + suffix;
    }
}
