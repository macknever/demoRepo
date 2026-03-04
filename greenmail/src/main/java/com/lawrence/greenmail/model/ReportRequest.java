package com.lawrence.greenmail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReportRequest(
        String bounceFrom,
        String from,
        String to,
        String subject,
        String body,
        String cc,
        int size
) {
}

