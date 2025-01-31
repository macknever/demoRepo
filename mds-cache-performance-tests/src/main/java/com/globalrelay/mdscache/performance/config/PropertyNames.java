package com.globalrelay.mdscache.performance.config;

/**
 * Helper class that holds string constants for configuration property names.
 */
public final class PropertyNames {
    public static final String JWT_MAXIMUM_VALIDITY_MINUTES_PROP = "jwt.maximum.validity.minutes";
    public static final String JWT_ALLOWED_ALGORITHMS_PROP = "jwt.allowed.algorithms";
    public static final String JWT_ALGORITHM_NAME_PROP = "jwt.algorithm.name";
    public static final String CACHE_API_OPERATIONS_PROP = "cache.api.operations";
    public static final String JWT_CORE_ISSUER_PROP = "jwt.core.iss";
    public static final String JWT_CACHE_AUDIENCE_PROP = "jwt.cache.audience";
    public static final String JWT_CORE_KEY_ID_PROP = "jwt.core.key-id";
    public static final String JWT_CORE_PRIVATE_KEY_PROP = "jwt.core.ec-private-key";
    public static final String JWT_CORE_PUBLIC_KEY_PROP = "jwt.core.ec-public-key";

    private PropertyNames() {
        throw new UnsupportedOperationException("Class should not be instantiated");
    }
}
