package com.demo.stream;

import java.util.List;
import java.util.stream.Stream;

public class create {
    <T> Stream<T> turnListToStream(T... list) {
        return Stream.of(list);
    }


}
