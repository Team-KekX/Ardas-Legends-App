package com.ardaslegends.data.service;

import java.util.ArrayList;

public class Path {
    private final int _cost;
    private final ArrayList<String> _path;

    public Path(int cost, ArrayList<String> path) {
        this._cost = cost;
        this._path = path;
    }

    public int getCost() {
        return this._cost;
    }

    public ArrayList<String> getPath() {
        return this._path;
    }
}
