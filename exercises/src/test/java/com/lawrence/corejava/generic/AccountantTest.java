package com.lawrence.corejava.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccountantTest {

    @Test
    void genericInterfaceShouldWork() {
        Accountant a = new Accountant(1.0d, "Susan");
        Accountant b = new Accountant(2.0d, "Emily");

        final double expectedSum = 5.0d;
        final Accountant expectedAcc = new Accountant(-1.0d, "John");
        assertEquals(expectedSum, a.sum(2.0, 3.0));
        assertEquals(expectedAcc, a.subtract(b));

    }

}
