package com.lawrence.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lawrence.corejava.stream.StreamExercises;

class StreamExercisesTest {
    private StreamExercises exercises;

    @BeforeEach
    void init() {
        exercises = new StreamExercises();
    }

    @Test
    void averageShouldWork() {
        List<Integer> nums = Stream.iterate(1, n -> n + 1).limit(100).toList();
        double expect = 50.5d;
        double actual = exercises.average(nums);
        assertEquals(expect, actual);
    }

    @Test
    void toUpperCaseShouldWork() {
        List<String> strings = Stream.of("qwe", "asd", "zxc", "qaz").toList();
        List<String> actualStr = exercises.toUpperCase(strings);
        assertEquals("QWE", actualStr.get(0));
    }

    @Test
    void sumOfEvenAndOddShouldWork() {
        List<Integer> nums = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toList();
        Map<Boolean, Integer> sums = exercises.sumOfEvenAndOdd(nums);
        assertEquals(30, sums.get(true));
        assertEquals(25, sums.get(false));
    }

    @Test
    void removeDuplicatedShouldWork() {
        List<String> list = Stream.of("qwe", "qwe", "qwe", "ewq").toList();
        List<String> distinctList = exercises.removeDuplicate(list);
        assertEquals(2, distinctList.size());
    }

}
