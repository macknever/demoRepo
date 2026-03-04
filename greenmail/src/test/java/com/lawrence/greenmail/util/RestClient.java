package com.lawrence.greenmail.util;

import java.util.function.Supplier;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestClient {
    private Supplier<RequestSpecBuilder> builderSupplier;

    public RestClient() {
    }

    public RestClient(Supplier<RequestSpecBuilder> builderSupplier) {
        this.builderSupplier = builderSupplier;
    }

    public Response postCall(final String path, final Object body) {
        RequestSpecification reqSpec = RestAssured.given().spec(builderSupplier.get().build());

        return reqSpec
                .basePath(path)
                .body(body)
                .post();

    }
}
