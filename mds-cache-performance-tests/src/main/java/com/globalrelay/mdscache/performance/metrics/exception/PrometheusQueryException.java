package com.globalrelay.mdscache.performance.metrics.exception;

/**
 * To encapsulate errors and exceptions that are thrown during the Prometheus query
 */
public class PrometheusQueryException extends RuntimeException {

    public PrometheusQueryException(Throwable e) {
        super(e);
    }

    public PrometheusQueryException(String message) {
        super(message);
    }
}
