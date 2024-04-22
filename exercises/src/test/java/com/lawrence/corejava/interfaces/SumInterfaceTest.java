package com.lawrence.corejava.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SumInterfaceTest {

    @Test
    void explicitDefineFunctionalInterface() {
        // one can use lambda to define interface
        SumInterface sumInterface = (a, b) -> a + b;

        assertEquals(5, sumInterface.sum(2, 3));
    }

    @Test
    void useLambda() {
        SumInterface sumInterface = (a, b) -> a * a + b * b;
        assertEquals(13, getSquareSum(sumInterface, 2, 3));
    }

    private int getSquareSum(SumInterface sumInterface, int a, int b) {
        return sumInterface.sum(a, b);
    }

}
