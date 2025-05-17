package ru.ssau.backend.service;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author ukolov-victor
 */

@Service
public class MLService {

    @Value("http://ml:8000/audio/v1/analyze")
    private String analyzeAudioEndpoint;

    @Value("s3_filename")
    private String analyzeS3ParameterName;

    @Value("model")
    private String modelParameterName;

    private final RestTemplate restTemplate;

    @Autowired
    public MLService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(
            value = {RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2
            )
    )
    public ResponseEntity<String> Notify(String uuid, String model) {
        URI uri = UriComponentsBuilder.fromHttpUrl(analyzeAudioEndpoint)
                .queryParam(modelParameterName, model)
                .build()
                .toUri();
        return restTemplate.postForEntity(
                uri,
                new HttpEntity<>(
                        Collections.singletonMap(analyzeS3ParameterName, uuid),
                        new HttpHeaders() {{
                            setContentType(MediaType.APPLICATION_JSON);
                        }}
                ),
                String.class
        );
    }
}
