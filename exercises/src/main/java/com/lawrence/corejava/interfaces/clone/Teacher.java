package com.lawrence.corejava.interfaces.clone;

public class Teacher {

    private String department;

    private String name;

    public Teacher(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return this.department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Teacher clone() throws CloneNotSupportedException {
        return (Teacher) super.clone();
    }
}
