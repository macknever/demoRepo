package com.globalrelay.mdscache.performance.metrics;

import java.util.Map;

/**
 * Holder that contains data for Gatling's statsEngine and reports
 */
public class ResponseBucketHolder {
    private final Map<String, Map<Double, Long>> responseBucket;
    private final long lastFetchTime;
    private final long delay;

    public ResponseBucketHolder(Map<String, Map<Double, Long>> responseBucket, long lastFetchTime, long delay) {
        this.responseBucket = responseBucket;
        this.lastFetchTime = lastFetchTime;
        this.delay = delay;
    }

    public Map<String, Map<Double, Long>> getResponseBucket() {
        return responseBucket;
    }

    public long getLastFetchTime() {
        return lastFetchTime;
    }

    public long getDelay() {
        return delay;
    }

    public boolean isEmpty() {
        return responseBucket.isEmpty();
    }
}
