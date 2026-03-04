package com.lawrence.pizza.models.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lawrence.pizza.models.OrderStatus;

public record StatusUpdate(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("oldStatus") OrderStatus oldStatus,
        @JsonProperty("newStatus") OrderStatus newStatus,
        @JsonProperty("updatedAt") long updatedAt,
        @JsonProperty("estimatedCompletion") long estimatedCompletion
) {}
