package com.lawrence.pizza.infrastructure;

public class Addresses {
    private Addresses() {}
    // --- Commands (Point-to-Point / Request-Reply) ---
    // Usually handled by one specific consumer
    public static final String ORDER_ORCHESTRATOR = "cmd.order.orchestrator";
    public static final String KITCHEN_SERVICE = "cmd.kitchen.service";

    // --- Internal Transitions ---
    public static final String STATUS_INTERNAL = "internal.status.transition";

    // --- Events (Publish/Subscribe) ---
    // Broadcast to many listeners (Tracker, Analytics, Emailer)
    public static final String ORDER_STATUS_UPDATES = "evt.order.status.updates";
}
