package com.lawrence.corejava.generic;

public interface Calculator<T> {
    <T> T sum(T a, T b);
    <R> R subtract(R b);

}
