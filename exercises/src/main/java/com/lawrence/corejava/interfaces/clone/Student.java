package com.lawrence.corejava.interfaces.clone;

public class Student extends Person {

    public void setMajor(String major) {
        this.major = major;
    }

    private String major;

    public Student(String name) {
        super(name);
    }

    public String getMajor() {
        return major;
    }

}
