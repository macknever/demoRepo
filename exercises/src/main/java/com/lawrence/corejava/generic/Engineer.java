package com.lawrence.corejava.generic;

public class Engineer extends Employee {

    private String department;

    public Engineer(String name,  String department) {
        super(name);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
