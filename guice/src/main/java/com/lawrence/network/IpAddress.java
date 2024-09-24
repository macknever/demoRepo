package com.lawrence.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.RestrictedBindingSource;

import jakarta.inject.Qualifier;

@RestrictedBindingSource(
        explanation = "Please install NetworkModule instead of binding network bindings yourself.",
        permits = { NetworkPermit.class }
)
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface IpAddress {
}
