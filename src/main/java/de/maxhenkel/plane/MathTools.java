package de.maxhenkel.plane;

public class MathTools {

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    public static float round(float value, int scale) {
        return (float) round((double) value, scale);
    }
}
