package ru.nsu.fit.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.max;

public class ImageViewPanel extends JPanel {
    private BufferedImage image;

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        if (image == null) {
            g.drawString("Изображение не загружено", getWidth() / 2 - 50, getHeight() / 2 - 10);
            return;
        }

        int rawImageWidth = image.getWidth();
        int rawImageHeight = image.getHeight();

        double widthProportion = (double) rawImageWidth / getWidth();
        double heightProportion = (double) rawImageHeight / getHeight();

        double proportion = max(widthProportion, heightProportion);

        int imageWidth = (int) (rawImageWidth / proportion);
        int imageHeight = (int) (rawImageHeight / proportion);

        g.drawImage(image, (getWidth() - imageWidth) / 2, (getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }
}
