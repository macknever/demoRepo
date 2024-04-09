package com.lawrence.stream;

import java.util.List;
import java.util.function.Function;

public class StreamExercises {


    public double average(List<Integer> nums) {
        return nums.stream().mapToInt(num -> num).average().orElse(0.0d);
    }

    public List<String> toUpperCase(List<String> strings) {
        return strings.stream().map(String::toUpperCase).toList();
    }




}
