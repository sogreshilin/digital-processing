package ru.nsu.fit.model;

import lombok.Getter;
import ru.nsu.fit.utils.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class ImageModel {
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int GRADIENT = 0;
    public static final int ANGLE = 1;

    private final double[][][] data;
    @Getter
    private final int width;
    @Getter
    private final int height;

    public ImageModel(BufferedImage image) {
        this(image.getWidth(), image.getHeight(), new double[image.getHeight()][image.getWidth()][3]);
        forEachPixel((y, x) -> {
            Color rawColor = new Color(image.getRGB(x, y));
            data[y][x][RED] = rawColor.getRed();
            data[y][x][GREEN] = rawColor.getGreen();
            data[y][x][BLUE] = rawColor.getBlue();
        });
    }

    public ImageModel(int width, int height, double[][][] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    /**
     * Переносит в изображение все изменения, накопленные в data
     */
    public BufferedImage export() {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        forEachPixel((y, x) -> result.setRGB(x, y, new Color(
                toNormalValue(data[y][x][RED]),
                toNormalValue(data[y][x][GREEN]),
                toNormalValue(data[y][x][BLUE])
        ).getRGB()));
        return result;
    }

    public double[] get(int y, int x) {
        return data[y][x];
    }

    public void forEachPixel(BiConsumer<Integer, Integer> action) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                action.accept(y, x);
            }
        }
    }

    public double applyKernel(int y, int x, Matrix kernel) {
        int radius = kernel.center();
        double value = 0;

        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                int ry = max(0, min(data.length - 1, y + dy));
                int rx = max(0, min(x + dx, data[ry].length - 1));

                value += kernel.get(dy + radius, dx + radius) * data[ry][rx][RED];
            }
        }

        return value;
    }

    private int toNormalValue(double value) {
        return max(0, Integer.min(255, (int) value));
    }

    public ImageModel copy() {
        return new ImageModel(width, height, copyArray(data, new double[height][width][3]));
    }

    private static double[][][] copyArray(double[][][] from, double[][][] to) {
        for(int i = 0; i < from.length; ++i){
            for(int j = 0; j < from[i].length; ++j){
                for(int k = 0; k < from[i][j].length; ++k){
                    to[i][j][k] = from[i][j][k];
                }
            }
        }
        return to;
    }

    public ImageModel copyEmpty() {
        return new ImageModel(width, height, new double[height][width][3]);
    }
}
