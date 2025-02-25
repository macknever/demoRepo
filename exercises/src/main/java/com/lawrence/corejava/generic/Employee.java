package com.lawrence.corejava.generic;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
