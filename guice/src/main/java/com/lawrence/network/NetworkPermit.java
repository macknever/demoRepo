package com.lawrence.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.RestrictedBindingSource;

@RestrictedBindingSource.Permit
@Retention(RetentionPolicy.RUNTIME)
public @interface NetworkPermit {
}

