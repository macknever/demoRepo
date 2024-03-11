package com.demo.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwitchDemoTest {

    @Test
    void switchShouldWorkTheOldWay() {
        SwitchDemo demo = new SwitchDemo();
        String ss = "ss";
        String res = demo.extend(ss);
        assertEquals("sss", res);
    }

}