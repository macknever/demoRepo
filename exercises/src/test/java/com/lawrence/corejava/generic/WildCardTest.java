package com.lawrence.corejava.generic;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class WildCardTest {

    @Test
    void wildCardExample() {
        List<Box> envelops = new ArrayList<>();
        Envelop envelop1 = new Envelop(1.1d, "220 Cambie");
        Envelop envelop2 = new Envelop(1.2d, "220 Cambie");
        Envelop envelop3 = new Envelop(1.3d, "220 Cambie");
        Envelop envelop4 = new Envelop(1.4d, "220 Cambie");
        Box box = new Box(1000d);
        envelops.add(envelop1);
        envelops.add(envelop2);
        envelops.add(envelop3);
        envelops.add(envelop4);
        envelops.add(box);

        Wrapper wrapper = new Wrapper();
        wrapper.process(envelops);

    }

}
