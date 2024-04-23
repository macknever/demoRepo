package com.lawrence.corejava.generic;

public class Envelop extends Box {

    public Envelop(double volume, String address) {
        super(volume);
        this.address = address;
    }

    private String address;

    public Envelop(double volume) {
        super(volume);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
