package com.lawrence.pizza.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@DataObject // This tells Vert.x to help with the mapping
public class Order {
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private List<PizzaItem> items;
    private double totalAmount;

    // Standard no-args constructor (Required for Jackson)
    public Order() {
    }

    public Order(JsonObject json) {
        this.orderId = json.getString("orderId");
        this.customerId = json.getString("customerId");
        this.status = OrderStatus.valueOf(json.getString("status"));
        this.items = json.getJsonArray("items").stream().map(PizzaItem.class::cast).toList();
        this.totalAmount = json.getDouble("totalAmount");
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<PizzaItem> getItems() {
        return items;
    }

    public void setItems(List<PizzaItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
