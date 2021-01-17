package ru.nsu.fit.model.filters;

import ru.nsu.fit.model.ImageModel;
import ru.nsu.fit.utils.Matrix;

import java.awt.image.BufferedImage;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.nsu.fit.model.ImageModel.*;

abstract class FilterBase {
    /**
     * Создаёт копию исходного изображения с применённой свёрткой
     */
    ImageModel applyKernel(ImageModel image, Matrix kernel) {
        ImageModel copy = image.copyEmpty();

        copy.forEachPixel((y, x) -> {
            double value = image.applyKernel(y, x, kernel);
            double[] pixel = copy.get(y, x);
            pixel[RED] = value;
            pixel[GREEN] = value;
            pixel[BLUE] = value;
        });

        return copy;
    }

    ImageModel grayScale(ImageModel image) {
        image.forEachPixel((y, x) -> {
            double[] base = image.get(y, x);
            double gray = base[RED] * 0.299 + base[GREEN] * 0.587 + base[BLUE] * 0.114;
            base[RED] = gray;
            base[GREEN] = gray;
            base[BLUE] = gray;
        });

        return image;
    }

    ImageModel normalize(ImageModel imageModel) {
        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;

        for (int x = 0; x < imageModel.getWidth(); ++x) {
            for (int y = 0; y < imageModel.getHeight(); ++y) {
                double v = imageModel.get(y, x)[RED];
                max = max(max, v);
                min = min(min, v);
            }
        }

        for (int x = 0; x < imageModel.getWidth(); ++x) {
            for (int y = 0; y < imageModel.getHeight(); ++y) {
                double v = imageModel.get(y, x)[RED];
                double newV = 255.0 * ((v - min) / (max - min));
                imageModel.get(y, x)[RED] = newV;
                imageModel.get(y, x)[GREEN] = newV;
                imageModel.get(y, x)[BLUE] = newV;
            }
        }

        return imageModel;
    }

    abstract public BufferedImage filter();
}
