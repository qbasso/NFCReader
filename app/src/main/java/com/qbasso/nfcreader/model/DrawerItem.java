package com.qbasso.nfcreader.model;


public class DrawerItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REGULAR = 1;

    private int type = 0;
    private String value;

    public DrawerItem(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
