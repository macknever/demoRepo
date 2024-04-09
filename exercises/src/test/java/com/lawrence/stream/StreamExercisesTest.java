package com.lawrence.stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Stream;

class StreamExercisesTest {
    private StreamExercises exercises;

    @BeforeEach
    void init() {
        exercises = new StreamExercises();
    }

    @Test
    void averageShouldWork() {
        List<Integer> nums = Stream.iterate(1, n -> n+1).limit(100).toList();
        double expect = 50.5d;
        double actual = exercises.average(nums);
        Assertions.assertEquals(expect, actual);
    }

    @Test
    void toUpperCaseShouldWork() {
        List<String> strings = Stream.of("qwe","asd","zxc","qaz").toList();
        List<String> actualStr = exercises.toUpperCase(strings);
        Assertions.assertEquals("QWE", actualStr.get(0));
    }

}