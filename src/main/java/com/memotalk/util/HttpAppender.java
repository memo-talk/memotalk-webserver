package com.memotalk.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HttpAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String remoteHost = "https://memo-elk.loca.lt";

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("message", eventObject.getFormattedMessage());
            logMap.put("level", eventObject.getLevel().toString());
            logMap.put("timestamp", eventObject.getTimeStamp());

            String json = objectMapper.writeValueAsString(logMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(remoteHost, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                addError("Failed to send log message: " + response.getStatusCode());
            } else {
                System.out.println("Successfully sent log message: " + response.getBody());
            }

        } catch (Exception e) {
            addError("Could not send log message", e);
        }
    }
}