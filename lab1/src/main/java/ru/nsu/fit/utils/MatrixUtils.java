package ru.nsu.fit.utils;

import static java.lang.Math.*;

public class MatrixUtils {
    public static Matrix gaussKernel(int size, double sigma) {
        Matrix kernel = new Matrix(size);
        int center = (int) floor(size / 2.0);
        kernel.forEach((y, x) -> {
            int dx = x - center;
            int dy = y - center;
            kernel.set(y, x, exp(-(dx * dx + dy * dy) / (2 * sigma * sigma)) / (2 * Math.PI * sigma * sigma));
        });
        return kernel;
    }

    public static Matrix gaborKernel(int size, double sigma, double gamma, double lambda, double theta) {
        Matrix kernel = new Matrix(size);
        int center = (int) floor(size / 2.0);

        double thetaRad = toRadians(theta);
        double sigma_x = sigma;
        double sigma_y = sigma / gamma;

        kernel.forEach((y, x) -> {
            double dx = x - center;
            double dy = y - center;
            double xPhi = dx * cos(thetaRad) + dy * sin(thetaRad);
            double yPhi = -dx * sin(thetaRad) + dy * cos(thetaRad);
            double gaborFunc = exp((-1.0 / 2.0) * (xPhi * xPhi / (sigma_x * sigma_x) + yPhi * yPhi / (sigma_y * sigma_y)))
                    * cos((2.0 * Math.PI / lambda) * xPhi);

            double globalFactor = 1.0 / (size * size);
            kernel.set(y, x, globalFactor * gaborFunc);
        });

        return kernel;
    }
}
