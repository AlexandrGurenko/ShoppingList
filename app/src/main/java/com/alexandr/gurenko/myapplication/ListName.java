package com.alexandr.gurenko.myapplication;

public class ListName {

    private int id;
    private String name;

    public ListName(){}

    public ListName(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ListName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
