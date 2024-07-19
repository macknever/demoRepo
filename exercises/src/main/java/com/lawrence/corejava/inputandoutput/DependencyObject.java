package com.lawrence.corejava.inputandoutput;

import java.io.Serializable;

public class DependencyObject implements Serializable {
    private String name;

    public DependencyObject(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
