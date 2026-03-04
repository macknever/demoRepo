package com.lawrence.pizza.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PizzaItem(
        @JsonProperty("productId") String productId,
        @JsonProperty("name") String name,
        @JsonProperty("size") String size, // e.g., "SMALL", "MEDIUM", "LARGE"
        @JsonProperty("quantity") int quantity,
        @JsonProperty("price") double price
) {}
