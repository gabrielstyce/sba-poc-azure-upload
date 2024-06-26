package com.example.demo.factory;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

@NoArgsConstructor
@AllArgsConstructor
public class HeaderFileNameFactory implements FileNameFactory {

    private String header;

    @Override
    public String get(ClientHttpResponse response) {
        Assert.hasText(header, "No header provided!");
        Assert.notNull(response, "ClientHttpResponse not provided!");
        return response.getHeaders().getFirst(header);
    }
}
