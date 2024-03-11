package com.demo.structures;

public class SwitchDemo {
    public String extend(String str) {
        switch (str) {
            case "ss":
                return "sss";
            case "dd":
                return "ddd";
            default:
                return str + str;
        }
    }

    public String extendNew(String str) {
        switch (str) {
            case "ss", "dd" -> {
                return "ss17";
            }

            default -> {
                return str + str;
            }
        }
    }
}
