// This code based on an example found at: http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/#.W3ZtZOhKiHs

package com.example.pedometer;

import name.tutorial1.tutorial7.StepCounterService;

public class StepDetector {

    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    // change this threshold according to adjust sensitivity of step detection
    private static final float STEP_THRESHOLD = 50f;

    // this time (in ns) sets how frequently we are looking for steps
    private static final int STEP_DELAY_NS = 250000000;

    //setting up an array for acceleration values (for each of the x, y, and z directions)
    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];

    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];

    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private com.example.pedometer.StepListener listener;
    public void registerListener(StepCounterService listener) {
        this.listener = (com.example.pedometer.StepListener) listener;
    }

    // gets acceleration values for x, y, z, then determines if they are a step or not
    public void updateAccel(long timeNs, float x, float y, float z) {

        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to get an estimate of the global z axis direction, based on all previous
        //   samples in the array. Note the % operation makes the counter "wrap" around to the start
        //   of the array
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        // Get the average of all accelerations recorded so far by summing all array entries
        //   for each axis, then dividing it by the number of entries (which is the minimum of
        //   the accelRingCounter or ACCEL_RING_SIZE because the array may not be full yet)
        float[] worldZ = new float[3];
        worldZ[0] = com.example.pedometer.SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = com.example.pedometer.SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = com.example.pedometer.SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        // Normalize the vector
        float normalization_factor = com.example.pedometer.SensorFilter.norm(worldZ);
        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        // determine how close the current acceleration vector is to the world z (i.e., if it is
        //   close to 1, they are aligned
        float currentZ = com.example.pedometer.SensorFilter.dot(worldZ, currentAccel) - normalization_factor;

        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = com.example.pedometer.SensorFilter.sum(velRing);

        //if the resulting value is over the step detection threshold and has happened after the
        // time threshold, and the PREVIOUS value is below the step threshold,
        // then we consider the new value a step
        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
            listener.stepDetected();
            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }
}

