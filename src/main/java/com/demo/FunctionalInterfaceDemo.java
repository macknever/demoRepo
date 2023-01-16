package com.demo;

import java.util.function.Supplier;

public class FunctionalInterfaceDemo {

    public double squareLazy(Supplier<Double> lazyValue) {
        return Math.pow(lazyValue.get(), 2);
    }



}
