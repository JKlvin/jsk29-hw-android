/**
 * ShakeEventManager is used to listen for shake events.
 * When an even occurs this manager will determine is the
 * shaking is hard enough.  If so the onShake method is called
 * to perform the desired activity.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class ShakeEventManager implements SensorEventListener {

	// The gForce that is necessary to register as shake. Must be greater than 1G (one earth gravity unit)
    private static final float SHAKE_THRESHOLD_GRAVITY = 1.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener listener;
    private long mShakeTimestamp;
    private int mShakeCount;

    /*
     * This method is used to listen for onShakeEvents
     * 
     * @param OnShakeListener listener for shake events.
     */
    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    /*
     * This is an interface so the OnShakeListener can call the proper 
     * method when a shake event occurs.  Need to be overridden.
     */
    public interface OnShakeListener {
        public void onShake(int count);
    }

    /*
     * (non-Javadoc)
     * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    /*
     * (non-Javadoc)
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (listener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now ) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now ) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                listener.onShake(mShakeCount);
            }
        }
    }
}
