package com.lawrence.corejava.inputandoutput;

import java.io.Serializable;

public class ToBeReadObj implements Serializable {
    private String name;
    private String id;

    private DependencyObject object;

    public ToBeReadObj(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DependencyObject getObject() {
        return object;
    }

    public void setObject(DependencyObject object) {
        this.object = object;
    }
}
