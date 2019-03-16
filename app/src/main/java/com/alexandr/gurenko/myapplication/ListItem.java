package com.alexandr.gurenko.myapplication;

public class ListItem {

    private int id;
    private String item;
    private int listNameId;

    public ListItem() {}

    public ListItem(String item) {
        this.item = item;
    }

    public ListItem(int id, String item) {
        this.id = id;
        this.item = item;
    }

    public ListItem(int id, String item, int listNameId) {
        this.id = id;
        this.item = item;
        this.listNameId = listNameId;
    }

    public String getItem() {
        return item;
    }

    public void setItems(String item) {
        this.item = item;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getListNameId() {
        return listNameId;
    }

    public void setListNameId(int listNameId) {
        this.listNameId = listNameId;
    }
}
