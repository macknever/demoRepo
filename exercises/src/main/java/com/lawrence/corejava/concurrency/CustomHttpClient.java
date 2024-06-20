package com.lawrence.corejava.concurrency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class CustomHttpClient {

    private static final String GOOGLE_URL = "https://www.google.ca/";
    private final HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create(GOOGLE_URL)).GET().build();
    CompletableFuture<HttpResponse<String>> f = client.sendAsync(
            request, HttpResponse.BodyHandlers.ofString());
}
