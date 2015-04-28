package com.demo.realita;

public enum ConstructionType {
    brick,
    wood,
    lowenergy,
    prefab,
    rest;

    private String text = "";

    public void setString(String t) {
        this.text = t;
    }
    @Override
    public String toString() {
        return text;
    }
}

