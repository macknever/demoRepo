package com.lawrence.corejava.generic;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {

    public void process(List<? extends Box> boxes) {
        for (var box : boxes) {
            System.out.println(box.getVolume());
        }
    }

    //Why this method does not work
    public void addAsEngineer(Engineer engineer , List<? super Engineer> engineers) {
        engineers.add(engineer);
    }
}
