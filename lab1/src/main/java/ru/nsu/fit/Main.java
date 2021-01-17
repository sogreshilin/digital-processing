package ru.nsu.fit;

import ru.nsu.fit.model.Model;
import ru.nsu.fit.view.MainWindow;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        MainWindow mainWindow = new MainWindow(model);
        mainWindow.show();
    }
}
