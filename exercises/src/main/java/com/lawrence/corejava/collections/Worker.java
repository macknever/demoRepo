package com.lawrence.corejava.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Worker {
    private final Map<String, Integer> name2Age;

    public Worker() {
        this.name2Age = new TreeMap<>();
    }

    public Worker(Map<String, Integer> name2Age) {
        this.name2Age = name2Age;
    }

    public Worker(Comparator<String> comparator) {
        this.name2Age = new TreeMap<>(comparator);
    }

    public Map<String, Integer> getName2Age() {
        return name2Age;
    }

    public void setName2Age(Map<String, Integer> name2Age) {
        this.name2Age.putAll(name2Age);
    }

    public void insert(final String name, final int age) {
        name2Age.put(name, age);
    }

    public void print() {
        System.out.println(this.name2Age);
    }
}
