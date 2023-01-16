package com.demo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class FunctionalInterfaceTest {

    @Test
    void supplierShouldWork() {
        FunctionalInterfaceDemo demo =  new FunctionalInterfaceDemo();
        Supplier<Double> supplier = () -> 9d;
        System.out.println(demo.squareLazy(supplier));

        List<String> names = Arrays.asList("Angela", "Aaron", "Bob", "Claire", "David");

        List<String> namesWithA = names.stream()
            .filter(name -> name.startsWith("A"))
            .collect(Collectors.toList());

        System.out.println(namesWithA);

    }
}
