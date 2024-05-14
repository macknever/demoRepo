package com.lawrence.corejava.generic;

import java.util.List;
import java.util.Objects;

public class Accountant extends Employee implements Calculator<Double, Accountant> {

    private Double value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Accountant that = (Accountant) o;
        return Objects.equals(value, that.value);
    }

    public Accountant(Double value, String name) {
        super(name);
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Double sum(Double a, Double b) {
        return a + b;
    }

    @Override
    public Accountant subtract(Accountant b) {
        this.value -= b.value;
        return this;
    }

    @Override
    public Integer sum(List<Double> list) {
        return null;
    }
}
