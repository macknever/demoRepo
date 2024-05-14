package com.lawrence.corejava.collections;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class WorkerTest {

    @Test
    void treeMapShouldWork() {
        Worker worker = new Worker();
        worker.insert("a",12);
        worker.insert("b",13);
        worker.insert("c",16);
        worker.insert("d",1);
        worker.insert("ee",2);
        worker.insert("ae",5);
        worker.insert("ac",17);

        worker.print();

    }

    @Test
    void treeMapShouldWorkWithCustomizedComparator() {
        Worker worker = new Worker(Comparator.comparingInt(String::length));
        worker.insert("a",12);
        worker.insert("b",13);
        worker.insert("c",16);
        worker.insert("d",1);
        worker.insert("ee",2);
        worker.insert("ae",5);
        worker.insert("ac",17);
        worker.insert("asdf",123);
        System.out.println(worker.getName2Age().size());

        worker.print();

    }

}