package com.lawrence.corejava.stream;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamExercises {

    public double average(List<Integer> nums) {
        return nums.stream().mapToInt(num -> num).average().orElse(0.0d);
    }

    public List<String> toUpperCase(List<String> strings) {
        return strings.stream().map(String::toUpperCase).toList();
    }

    public Map<Boolean, Integer> sumOfEvenAndOdd(List<Integer> nums) {
        return nums.stream()
                .collect(Collectors.partitioningBy(x -> x % 2 == 0, Collectors.summingInt(Integer::intValue)));
    }

    public <T> List<T> removeDuplicate(List<T> list) {
        return list.stream().distinct().toList();
    }

}
