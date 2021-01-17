package ru.nsu.fit.model.filters;

import lombok.AllArgsConstructor;
import ru.nsu.fit.model.ImageModel;
import ru.nsu.fit.utils.MatrixUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.nsu.fit.model.ImageModel.RED;

@AllArgsConstructor
public class GaborFilter extends FilterBase {
    private BufferedImage rawImage;
    private double lambda;
    /**
     * Используется для сжатия по y
     */
    private double gamma;
    private double sigma;
    private List<Double> angles;
    private int endStep;

    public BufferedImage filter() {
        ImageModel model = new ImageModel(rawImage);

        List<Function<ImageModel, ImageModel>> steps = new ArrayList<>();

        steps.add(this::grayScale);
        steps.add((in) -> {
            ImageModel copy = in.copyEmpty();
            for(double theta : angles){
                ImageModel step = applyKernel(in, MatrixUtils.gaborKernel(5, sigma, gamma, lambda, theta));
                step.forEachPixel((y,x)-> copy.get(y, x)[RED] = copy.get(y, x)[RED] + step.get(y, x)[RED]);
            }
            return normalize(copy);
        });

        for (int i = 0; i < endStep; ++i) {
            model = steps.get(i).apply(model);
        }

        return model.export();
    }
}
