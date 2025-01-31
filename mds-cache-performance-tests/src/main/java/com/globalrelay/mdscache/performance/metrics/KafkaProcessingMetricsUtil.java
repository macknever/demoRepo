package com.globalrelay.mdscache.performance.metrics;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globalrelay.mdscache.performance.config.PropertiesInjector;
import com.globalrelay.mdscache.performance.helper.PropertiesHelper;
import com.globalrelay.mdscache.performance.metrics.exception.PrometheusQueryException;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Key;
import com.google.inject.name.Names;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Fetches Kafka event processing time histograms from Prometheus REST API and processes data to record them in
 * Gatling's statsEngine
 */
public class KafkaProcessingMetricsUtil {
    private static final String MISMATCHED_BUCKET_ERROR = "The buckets between current metrics and previous metrics " +
            "don't match";
    private static final String MISSING_STATUS_ERROR =
            "Previous metrics should map to two statuses, but is missing a status";
    private static final Logger LOG = LoggerFactory.getLogger(KafkaProcessingMetricsUtil.class);
    private static final String QUERY_ENDPOINT = "/api/v1/query";
    private static final String NAMESPACE = "uc-mds-cache";
    private static final String KAFKA_METER_METRICS_NAME = "http_kafka_message_meter_time_seconds_bucket";
    private static final long PROMETHEUS_SCRAPE_INTERVAL_MS = 30000L; // 30 seconds
    private static final int CONVERT_TO_MILLISECONDS = 1000;

    private final HttpClient httpClient;

    private final AtomicBoolean lock;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean initialized = new AtomicBoolean();

    private final String prometheusURL;
    private final String promQL;

    private Map<String, Map<Double, Long>> previousMetrics;
    private long delayMs = PROMETHEUS_SCRAPE_INTERVAL_MS;
    private long lastFetchTime;

    private KafkaProcessingMetricsUtil(@Nonnull final String prometheusURL) {
        this(prometheusURL, HttpClientBuilder.create().build());
    }

    @VisibleForTesting
    KafkaProcessingMetricsUtil(@Nonnull final String prometheusURL, HttpClient httpClient) {
        Objects.requireNonNull(prometheusURL);
        this.prometheusURL = prometheusURL;
        if (httpClient == null) {
            this.httpClient = HttpClientBuilder.create().build();
        } else {
            this.httpClient = httpClient;
        }

        promQL = "sum by (le, status) (" + KAFKA_METER_METRICS_NAME + "{namespace=\"" + getNamespace() + "\"})";

        lock = new AtomicBoolean(true);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Returns a new instance of {@link KafkaProcessingMetricsUtil}
     *
     * @return
     */
    public static KafkaProcessingMetricsUtil getInstance() {
        return new KafkaProcessingMetricsUtil(
                PropertiesInjector.getInstance().getInstance(Key.get(String.class,
                        Names.named("prometheus.endpoint.url"))));
    }

    /**
     * @param delayMs the time, in ms, in which the lock is locked to block fetching metrics
     */
    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

    /**
     * This initialization is required to store the state before running tests
     */
    public void initialize() {
        if (!initialized.getAndSet(true)) {
            lastFetchTime = System.currentTimeMillis();
            previousMetrics = fetchMetrics();
            schedule();
        }
    }

    /**
     * Fetches new records from Prometheus and subtract them from the previous records
     *
     * @return {@link ResponseBucketHolder}, which contains the difference from the last records
     */
    public ResponseBucketHolder getResponseTimeRecords() {
        Map<String, Map<Double, Long>> responseMap = Collections.emptyMap();
        long previousFetchTime = lastFetchTime;
        if (!lock.getAndSet(true)) {
            Map<String, Map<Double, Long>> currentMetrics = fetchMetrics();
            responseMap = subtractPreviousRecords(currentMetrics);
            previousMetrics = currentMetrics;

            lastFetchTime = System.currentTimeMillis();
            schedule();
        }

        return new ResponseBucketHolder(responseMap, previousFetchTime, delayMs);
    }

    /**
     * Is responsible for creating a map containing le (in ms) and corresponding counts each kafka event processed in
     * Mds-cache
     * <p>
     * First, it fetches histogram buckets through Prometheus REST API. Then it subtracts any previous (if any)
     * histogram values from the respective buckets in {@link #subtractCountFromPreviousBucket(Map)} because bucket
     * values contain inclusive values, each time previous scale bucket value is subtracted from the next scale bucket
     * to get the absolute number of records in a given range.
     * <p>
     * For e.g. say 0.25, 0.5, 0.75 are le threshold of the buckets. 100, 300, 800 are respective count values.
     * So, after subtraction, the absolute records in respective buckets are (<0.25, 100), (0.25< ~ <0.5, 200(300-100))
     * , (0.5 < ~ 0.75, 500(800-300)).
     * <p>
     * The value of the le (less than or equal) label in the metrics indicates the upper bound of the time duration.
     * It's impossible to estimate individual processing time exactly, so it involves randomness during the estimation,
     * which inevitably distorts the real statistics.
     * <p>
     *
     * @return the map containing time durations as the key and respective counts as the value
     */
    private Map<String, Map<Double, Long>> fetchMetrics() {
        HttpGet httpGet = new HttpGet(prometheusURL + QUERY_ENDPOINT);

        HttpResponse response;
        JsonObject jsonObject;
        try {
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("query", promQL)
                    .build();

            httpGet.setURI(uri);

            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error("Status from Prometheus API was not 200 but {}", response.getStatusLine().getStatusCode());
                throw new PrometheusQueryException("Bad request or Prometheus Server Error");
            }

            // Sample response from Prometheus API: "{"status":"success","data":{"resultType":"vector","result":[]}}"
            jsonObject = new JsonObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            if ("error".equals(jsonObject.getString("status"))) {
                LOG.error("There was an error in the response from Prometheus API: {}", jsonObject);
                throw new PrometheusQueryException("Bad promQL: " + promQL);
            }
        } catch (URISyntaxException | IOException e) {
            throw new PrometheusQueryException(e);
        }

        LOG.debug("The response from Prometheus: {}", jsonObject);

        Map<String, Map<Double, Long>> le2countMetricsMap = new HashMap<>();

        JsonArray metrics = jsonObject.getJsonObject("data").getJsonArray("result");
        // If Pods are fresh, then no data have been recorded, so the result can be empty
        if (!metrics.isEmpty()) {
            Map<Double, Long> successMap = new TreeMap<>();
            Map<Double, Long> failureMap = new TreeMap<>();

            le2countMetricsMap.put("success", successMap);
            le2countMetricsMap.put("failure", failureMap);

            metrics.stream().forEach(object -> {
                // the unit of le is second, so it needs the conversion to millisecond later
                String le = ((JsonObject) object).getJsonObject("metric").getString("le");
                String count = ((JsonObject) object).getJsonArray("value").getString(1);
                String status = ((JsonObject) object).getJsonObject("metric").getString("status");

                le2countMetricsMap.get(status).put("+Inf".equals(le) ? Double.MAX_VALUE :
                        (Double.parseDouble(le) * CONVERT_TO_MILLISECONDS), Long.parseLong(count));
            });

            subtractCountFromPreviousBucket(le2countMetricsMap);
        }

        return le2countMetricsMap;
    }

    private void schedule() {
        scheduler.schedule(() -> lock.compareAndSet(true, false), delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Subtracts the value from the previous bucket because the value in the bucket is cumulative
     *
     * @param metricsMap the map containing raw histograms obtained from Prometheus REST API
     */
    private void subtractCountFromPreviousBucket(Map<String, Map<Double, Long>> metricsMap) {
        metricsMap.forEach((k, v) -> {
            Long previousCount = 0L;
            for (Map.Entry<Double, Long> entry : v.entrySet()) {
                Long newCount = entry.getValue() - previousCount;
                previousCount = entry.getValue();
                entry.setValue(newCount);
            }
        });
    }

    /**
     * Subtracts each value from corresponding value in the previous metrics.
     *
     * @param metrics metrics containing the latest records
     * @return the map containing the difference between the previous and the new metrics
     */
    private Map<String, Map<Double, Long>> subtractPreviousRecords(Map<String, Map<Double, Long>> metrics) {
        final Map<String, Map<Double, Long>> deepCopy = new HashMap<>();
        if (!previousMetrics.isEmpty()) {
            Map<Double, Long> successMap = new TreeMap<>();
            Map<Double, Long> failureMap = new TreeMap<>();
            deepCopy.put("success", successMap);
            deepCopy.put("failure", failureMap);

            metrics.forEach((k, v) -> {
                Map<Double, Long> previousRecords = previousMetrics.get(k);

                if (previousRecords == null) {
                    LOG.error("{}; previous metrics: {}; current metrics: {}", MISSING_STATUS_ERROR, previousMetrics,
                            metrics);
                    throw new IllegalStateException(MISSING_STATUS_ERROR);
                }

                Map<Double, Long> currentRecords = deepCopy.get(k);
                for (Map.Entry<Double, Long> entry : v.entrySet()) {
                    final Long previousValue = previousRecords.get(entry.getKey());

                    if (previousValue == null) {
                        LOG.error("{}; previous metrics: {}; current metrics: {}", MISMATCHED_BUCKET_ERROR,
                                previousMetrics, metrics);
                        throw new IllegalStateException(MISMATCHED_BUCKET_ERROR);
                    }

                    currentRecords.put(entry.getKey(), entry.getValue() - previousValue);
                }
            });
        }

        return deepCopy.isEmpty() ? metrics : deepCopy;
    }

    private String getNamespace() {
        String target = PropertiesHelper.getTargetEnvironment();
        return "minikube".equals(target) ? (NAMESPACE + "-local") : (NAMESPACE);
    }

}
