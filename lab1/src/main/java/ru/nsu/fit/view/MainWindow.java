package ru.nsu.fit.view;

import ru.nsu.fit.model.Model;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private static final Dimension MIN_SIZE = new Dimension(1100, 700);

    private final JFrame mainFrame;
    private ImageViewPanel imageView;

    public MainWindow(Model model) {
        this.mainFrame = new JFrame();

        mainFrame.setMinimumSize(MIN_SIZE);
        mainFrame.setSize(MIN_SIZE);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        imageView = new ImageViewPanel();
        mainFrame.add(imageView, BorderLayout.CENTER);
        mainFrame.add(new ControlPanel(model, this), BorderLayout.WEST);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    public ImageViewPanel getImageView() {
        return imageView;
    }
}
