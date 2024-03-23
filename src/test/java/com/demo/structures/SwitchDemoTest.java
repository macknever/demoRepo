package com.demo.structures;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SwitchDemoTest {

    @Test
    void switchShouldWorkTheOldWay() {
        SwitchDemo demo = new SwitchDemo();
        String ss = "ss";
        String res = demo.extend(ss);
        String resNew = demo.extendNew(ss);
        assertEquals("sss", res);
        assertEquals("ss17", resNew);
    }

    @Test
    void castCanNotHappenFromMoreBitsToLessBits() {
        long l1 = 1232344234343443L;
        int i1 = (int) l1;
        System.out.println(i1);
    }

    @Test
    void ext() {
// Define variables
        String name = "Alice";
        int score = 85;
        double price = 19.99;

        // Format and print the string
        String formattedString = String.format(" Name: %2$-10s | Score: %3$5d | Price: %1$8.2f", price, name, score);
        System.out.println(formattedString);

        String formattingStr = String.format("%1$s, %1$2.2f", 23.5);
        System.out.println(formattingStr);
    }

    @Test
    void yieldTest() {
        String seasonName = "Spring";
        int numLetters = switch (seasonName)
        {
            case "Spring": {
                System.out.println(
                        "spring time!");
            }
            case "Summer", "Winter":{
                yield 6;}
            case "Fall":{
                yield 4;}
            default:{
                yield -1;}
        };

        System.out.println(numLetters);


    }

    @Test
    void yieldTest2() {
        String seasonName = "Spring";


        int numLetters = switch (seasonName)
        {
            case "Spring" ->
            {
                System.out.println(
                        "spring time!");
                yield 6;
            }
            case "Summer", "Winter" -> 6;
            case "Fall" -> 4;
            default -> -1;
        };

        System.out.println(numLetters);
    }

    @Test
    void streamShouldWork() {
        Stream<Integer> vals = Stream.iterate(0,n -> n < 10, n -> n+1);
        System.out.println(vals.toList());
        String s = "Hello World";
        List<String> outShapedS = s.codePoints().mapToObj(cp -> new String(new int[] { cp },0,1)).toList();
for(String str: outShapedS) {
    System.out.println(str);
}
    }


}