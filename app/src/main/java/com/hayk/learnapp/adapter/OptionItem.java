package com.hayk.learnapp.adapter;

/**
 * Created by User on 13.11.2017.
 */

public class OptionItem {
    private int icon;
    private String name;

    public OptionItem(int icon,String name){
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
