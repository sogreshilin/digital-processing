package ru.nsu.fit.view;

import ru.nsu.fit.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Панель с кнопками для установки параметров алгоритма
 */
public class ControlPanel extends JPanel {
    private static final int WIDTH = 200;
    private static final int COMPONENT_HEIGHT = 20;
    private static final Dimension MIN_SIZE = new Dimension(WIDTH, 600);
    private static final Dimension COMPONENT_SIZE = new Dimension(WIDTH, COMPONENT_HEIGHT);
    private Map<JComponent, Supplier<Boolean>> enabledSuppliers = new HashMap<>();
    private GridBagConstraints gbc = new GridBagConstraints();

    private JTextField kannyGaussSize;
    private JTextField kannyGaussSigma;
    private JTextField kannyThreesoldLow;
    private JTextField kannyThreesoldHigh;

    private JTextField gaborLambda;
    private JTextField gaborGamma;
    private JTextField gaborSigma;
    private JTextField gaborAngles;

    public ControlPanel(Model model, MainWindow mainWindow) {
        setUpLayout();
        mainWindow.getMainFrame().setTitle("Лаб 1");
        setSize(MIN_SIZE);
        setMinimumSize(MIN_SIZE);
        setPreferredSize(MIN_SIZE);

        addLabel("Настройки");
        addButton("Загрузить изображение", () -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./data"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = model.loadFile(selectedFile);

                mainWindow.getMainFrame().setTitle("Лаб 1." + ((model.getType() == null) ? "" : (" Тип: " + model.getType())));

                if (image == null) {
                    mainWindow.getMainFrame().setTitle("Лаб 1");
                    return;
                }

                mainWindow.getImageView().setImage(image);
                updateComponents();
            }
        }, () -> true);

        addLabel(" ");
        addLabel("Канни");
        addLabel("Размытие по Гауссу:");
        addLabel("Размер матрицы:");
        kannyGaussSize = addTextField("5", () -> model.getImage() != null);
        addLabel("Коэффициент:");
        kannyGaussSigma = addTextField("1.0", () -> model.getImage() != null);
        addLabel("Двойная пороговая фильтрация:");
        addLabel("Нижняя грань:");
        kannyThreesoldLow = addTextField("0.55", () -> model.getImage() != null);
        addLabel("Верхняя грань:");
        kannyThreesoldHigh = addTextField("0.60", () -> model.getImage() != null);

        Consumer<Integer> kannyStep = (step) -> {
            model.kannyFilter(
                    Integer.parseInt(kannyGaussSize.getText()),
                    Double.parseDouble(kannyGaussSigma.getText()),
                    Double.parseDouble(kannyThreesoldLow.getText()),
                    Double.parseDouble(kannyThreesoldHigh.getText()),
                    step);

            mainWindow.getImageView().setImage(model.getImage());
        };

        addButton("Grayscale", () -> kannyStep.accept(1), () -> model.getImage() != null);
        addButton("... -> Размытие", () -> kannyStep.accept(2), () -> model.getImage() != null);
        addButton("... -> Градиенты", () -> kannyStep.accept(3), () -> model.getImage() != null);
        addButton("... -> Подавление", () -> kannyStep.accept(4), () -> model.getImage() != null);
        addButton("... -> Фильтрация", () -> kannyStep.accept(5), () -> model.getImage() != null);
        addButton("... -> Трассировка", () -> kannyStep.accept(6), () -> model.getImage() != null);


        addLabel(" ");
        addLabel(" ");
        addLabel("Габор");
        addLabel("Лямбда:");
        gaborLambda = addTextField("3", () -> model.getImage() != null);
        addLabel("Гамма:");
        gaborGamma = addTextField("0.1", () -> model.getImage() != null);
        addLabel("Сигма:");
        gaborSigma = addTextField("1.68", () -> model.getImage() != null);
        addLabel("Углы:");
        gaborAngles = addTextField("0,30,60,90,120,150", () -> model.getImage() != null);

        Consumer<Integer> gaborStep = (step) -> {
            model.gaborFilter(
                    Double.parseDouble(gaborLambda.getText()),
                    Double.parseDouble(gaborGamma.getText()),
                    Double.parseDouble(gaborSigma.getText()),
                    Arrays.stream(gaborAngles.getText()
                            .replace(" ", "")
                            .split(","))
                            .map(Double::parseDouble)
                            .collect(toList()),
                    step);

            mainWindow.getImageView().setImage(model.getImage());
        };

        addButton("Grayscale", () -> gaborStep.accept(1), () -> model.getImage() != null);
        addButton("... -> Свертка", () -> gaborStep.accept(2), () -> model.getImage() != null);

        addFiller();
        updateComponents();
    }

    private void setUpLayout() {
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
    }

    /**
     * Добавляет растяжку в конце, которая заставляет все элементы собраться вверху панели
     */
    private void addFiller() {
        gbc.weighty = 1.0;
        add(new JPanel(), gbc);
        gbc = null;
    }

    private JButton addButton(String name, Runnable onClickAction, Supplier<Boolean> isEnabled) {
        JButton button = new JButton(name);
        button.setPreferredSize(COMPONENT_SIZE);
        button.addActionListener(e -> onClickAction.run());
        button.setEnabled(isEnabled.get());
        add(button, gbc);
        enabledSuppliers.put(button, isEnabled);
        gbc.gridy++;
        return button;
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(COMPONENT_SIZE);
        add(label, gbc);
        gbc.gridy++;
        return label;
    }

    private JTextField addTextField(String text, Supplier<Boolean> isEnabled) {
        JTextField textField = new JTextField(text);
        textField.setPreferredSize(COMPONENT_SIZE);
        add(textField, gbc);
        enabledSuppliers.put(textField, isEnabled);
        gbc.gridy++;
        return textField;
    }

    private void updateComponents() {
        enabledSuppliers.keySet().forEach(key -> key.setEnabled(enabledSuppliers.get(key).get()));
    }
}
