package com.lawrence.corejava.generic;

import java.util.List;

public class Wrapper {

    public void process(List<? extends Box> boxes) {
        for (var box : boxes) {
            System.out.println(box.getVolume());
        }
    }
}
