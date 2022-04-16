package com.madejamaciej.planer;

public class Random {
    public static int GetRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
