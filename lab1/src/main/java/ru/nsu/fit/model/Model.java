package ru.nsu.fit.model;

import ru.nsu.fit.model.filters.GaborFilter;
import ru.nsu.fit.model.filters.KannyFilter;
import ru.nsu.fit.utils.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    private BufferedImage image;
    private File lastFile;
    private String type;
    private Map<String, String> names = new HashMap<>();

    public Model() {
        names.put("0365", "365 nm LED illumination");
        names.put("0450", "450 nm LED illumination");
        names.put("0470", "470 nm LED illumination");
        names.put("0505", "505 nm LED illumination");
        names.put("0535", "535 nm LED illumination");
        names.put("0570", "570 nm LED illumination");
        names.put("0615", "615 nm LED illumination");
        names.put("0630", "630 nm LED illumination");
        names.put("0700", "700 nm LED illumination");
        names.put("0735", "735 nm LED illumination");
        names.put("0780", "780 nm LED illumination");
        names.put("0870", "870 nm LED illumination");
        names.put("0940", "940 nm LED illumination");
        names.put("RAIR", "raking infrared (940 nm) illumination from the right");
        names.put("RABR", "raking blue (470 nm) illumination from the right");
        names.put("RAIL", "raking infrared (940 nm) illumination from the left");
        names.put("RABL", "raking blue (470 nm) illumination the left");
        names.put("CFUR", "ultraviolet (365 nm) illumination with red color filter");
        names.put("CFUG", "ultraviolet (365 nm) illumination with green color filter");
        names.put("CFUB", "ultraviolet (365 nm) illumination with blue color filter");
        names.put("CFBR", "blue (450 nm) illumination with red color filter");
        names.put("CFBG", "blue (450 nm) illumination with green color filter");
        names.put("CFBB", "blue (450 nm) illumination with blue color filter");
        names.put("CFUX", "all three color filter ultraviolet images in combination (CFUR, CFUG, CFUB) used in color sharpie images");
    }

    public BufferedImage loadFile(File file) {
        try {
            image = ImageIO.read(file);
            lastFile = file;

            type = names.keySet()
                    .stream()
                    .filter(k -> file.getName().contains(k))
                    .findAny()
                    .map(k -> names.get(k))
                    .orElse(null);

            return image;
        } catch (Exception e) {
            Log.e(e, "Can't load image");
            return null;
        }
    }

    public String getType() {
        return type;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void kannyFilter(int gaussSize, double gaussSigma, double lowPercent, double highPercent, int endStep) {
        reloadLastFile();
        image = new KannyFilter(image, gaussSize, gaussSigma, lowPercent, highPercent, endStep).filter();
    }

    public void gaborFilter(double lambda, double gamma, double sigma, List<Double> angles, int endStep) {
        reloadLastFile();
        image = new GaborFilter(image, lambda, gamma, sigma, angles, endStep).filter();
    }

    public BufferedImage reloadLastFile() {
        return loadFile(lastFile);
    }
}
