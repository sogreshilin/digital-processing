package ru.nsu.fit.utils;

import java.util.function.BiConsumer;

import static java.lang.Math.floor;

public class Matrix {
    private final double[][] data;

    public Matrix(int size) {
        this(new double[size][size]);
    }

    public Matrix(double[][] data) {
        this.data = data;
    }

    public double get(int y, int x) {
        return data[y][x];
    }

    public void set(int y, int x, double value) {
        data[y][x] = value;
    }

    public void forEach(BiConsumer<Integer, Integer> action) {
        for (int y = 0; y < data.length; ++y) {
            for (int x = 0; x < data[y].length; ++x) {
                action.accept(y, x);
            }
        }
    }

    public int center() {
        return (int) (floor(data.length / 2.0));
    }

    public int size() {
        return data.length;
    }
}
