package com.lawrence.corejava.interfaces.clone;

public class Person implements Cloneable {
    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
