package com.lawrence.corejava.generic;

import java.util.List;

public interface Calculator<T, R> {
    T sum(T a, T b);

    R subtract(R b);

    Integer sum(List<T> list);
}
