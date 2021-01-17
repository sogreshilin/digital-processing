package ru.nsu.fit.model.filters;

import lombok.AllArgsConstructor;
import ru.nsu.fit.model.ImageModel;
import ru.nsu.fit.utils.Matrix;
import ru.nsu.fit.utils.MatrixUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.*;
import static ru.nsu.fit.model.ImageModel.*;

@AllArgsConstructor
public class KannyFilter extends FilterBase {
    private BufferedImage rawImage;
    private int gaussSize;
    private double gaussSigma;
    private double lowPercent;
    private double highPercent;
    private int endStep;

    public BufferedImage filter() {
        ImageModel model = new ImageModel(rawImage);

        List<Function<ImageModel, ImageModel>> steps = new ArrayList<>();

        steps.add(this::grayScale);
        steps.add((in) -> applyKernel(in, MatrixUtils.gaussKernel(gaussSize, gaussSigma)));
        steps.add(this::applyGradients);
        steps.add(this::supressNonMax);
        steps.add((in) -> doubleThreesold(in, lowPercent, highPercent));
        steps.add(this::blobAnalysis);

        for (int i = 0; i < endStep; ++i) {
            model = steps.get(i).apply(model);
        }

        return model.export();
    }

    private ImageModel blobAnalysis(ImageModel image) {
        ImageModel copy = image.copyEmpty();

        copy.forEachPixel((y, x) -> {
            double[] sourcePixel = image.get(y, x);
            double rawValue = sourcePixel[RED];

            double[] pixel = copy.get(y, x);

            if (rawValue < 10 || rawValue > 245) {
                pixel[RED] = rawValue;
                pixel[GREEN] = rawValue;
                pixel[BLUE] = rawValue;
                return;
            }

            for (int ry = y - 1; ry <= y + 1; ++ry) {
                for (int rx = x - 1; rx <= x + 1; ++rx) {
                    if (rx >= 0 && ry >= 0 && rx < image.getWidth() && ry < image.getHeight() && image.get(ry, rx)[RED] > 245) {
                        pixel[RED] = 255;
                        pixel[GREEN] = 255;
                        pixel[BLUE] = 255;
                        return;
                    }
                }
            }

            pixel[RED] = 0;
            pixel[GREEN] = 0;
            pixel[BLUE] = 0;
        });

        return copy;
    }

    private ImageModel doubleThreesold(ImageModel image, double lowPercent, double highPercent) {
        double low = 255 * lowPercent;
        double high = 255 * highPercent;

        image.forEachPixel((y, x) -> {
            double[] pixel = image.get(y, x);
            double rawValue = pixel[RED];

            double value;

            if (rawValue < low) {
                value = 0;
            } else if (rawValue < high) {
                value = 127;
            } else {
                value = 255;
            }

            pixel[RED] = value;
            pixel[GREEN] = value;
            pixel[BLUE] = value;
        });

        return image;
    }

    private ImageModel supressNonMax(ImageModel image) {
        ImageModel copy = image.copyEmpty();

        image.forEachPixel((y, x) -> {
            double[] sourcePixel = image.get(y, x);
            double g = sourcePixel[GRADIENT];
            double angle = sourcePixel[ANGLE];

            double value = g;
            if (g > 0) {
                int dx = (int) signum(cos(angle));
                int dy = (int) -signum(sin(angle));

                int rx = x + dx;
                int ry = y + dy;
                if (rx >= 0 && ry >= 0 && rx < image.getWidth() && ry < image.getHeight()) {
                    double rg = image.get(ry, rx)[GRADIENT];
                    if (rg > g) {
                        value = 0;
                    }
                }

                rx = x - dx;
                ry = y - dy;
                if (rx >= 0 && ry >= 0 && rx < image.getWidth() && ry < image.getHeight()) {
                    double rg = image.get(ry, rx)[GRADIENT];
                    if (rg > g) {
                        value = 0;
                    }
                }
            }


            double[] pixel = copy.get(y, x);
            pixel[RED] = value;
            pixel[GREEN] = value;
            pixel[BLUE] = value;
        });

        return copy;
    }

    private ImageModel applyGradients(ImageModel image) {
        ImageModel copy = image.copyEmpty();

        Matrix gradientX = new Matrix(new double[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}});
        Matrix gradientY = new Matrix(new double[][]{{1, 0, -1}, {2, 0, -2}, {1, 0, -1}});
        double angleStep = PI / 4.0;

        image.forEachPixel((y, x) -> {
            double gx = image.applyKernel(y, x, gradientX);
            double gy = image.applyKernel(y, x, gradientY);
            double g = sqrt(gx * gx + gy * gy);
            double angle = 0;
            if (abs(gx) > 0 || abs(gy) > 0) {
                angle = round(atan2(gx, gy) / angleStep) * angleStep;
            }

            double[] pixel = copy.get(y, x);
            pixel[GRADIENT] = g;
            pixel[ANGLE] = angle;
        });

        return copy;
    }
}
