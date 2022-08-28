package com.example.batchgeneralize.config;

import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

public class EndpointConfig {
    public static WebClient getGenericWebClient(String baseUrl) {
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder().codecs(ClientCodecConfigurer::defaultCodecs).build()).baseUrl(baseUrl).build();
    }
}
