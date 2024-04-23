package com.lawrence.corejava.generic;

public interface Calculator<T, R> {
    T sum(T a, T b);

    R subtract(R b);
}
