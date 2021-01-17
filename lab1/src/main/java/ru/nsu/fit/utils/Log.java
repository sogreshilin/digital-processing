package ru.nsu.fit.utils;

public class Log {
    public static void e(Throwable e, String text){
        System.err.println(text);
        e.printStackTrace();
    }
}
