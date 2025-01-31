package com.globalrelay.mdscache.performance

/**
 * Predef object provides default imports and utilities for Kafka simulations in Gatling.
 *
 * This object extends the KafkaDsl, making the DSL methods and objects available without
 * needing to import them explicitly.
 *
 * It acts as a shortcut to access Kafka simulation definitions and configuration methods.
 */
object Predef extends KafkaDsl
