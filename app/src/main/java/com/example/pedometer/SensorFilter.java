// This code based on an example found at: http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.W3ZtZOhKiHs


package com.example.pedometer;

public class SensorFilter {

    private SensorFilter() {
        //constructor
    }

    //sums all elements in a provided array, returning the total
    public static float sum(float[] array) {
        float total = 0;
        for (float anArray : array) {
            total += anArray;
        }
        return total;
    }

    //normalizes the values in a provided array, returning the normalized value
    public static float norm(float[] array) {
        float normValue = 0;
        for (float anArray : array) {
            normValue += anArray * anArray;
        }
        return (float) Math.sqrt(normValue);
    }

    //computes the dot product between two vectors provided as arrays
    public static float dot(float[] a, float[] b) {
        return (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2]);
    }
}
