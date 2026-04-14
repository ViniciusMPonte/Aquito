package com.pontevi.aquito.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BussolaSensor implements SensorEventListener {

    public interface AzimuteListener {
        void onAzimuteChanged(float azimute);
    }

    private final SensorManager sensorManager;
    private final AzimuteListener listener;

    private Sensor acelerometro;
    private Sensor campoMagnetico;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    public BussolaSensor(SensorManager sensorManager, AzimuteListener listener) {
        this.sensorManager = sensorManager;
        this.listener = listener;

        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        campoMagnetico = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void iniciar() {
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, campoMagnetico, SensorManager.SENSOR_DELAY_UI);
    }

    public void parar() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values.clone();

        float[] R = new float[9];
        float[] I = new float[9];

        boolean sucesso = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        if (!sucesso) return;

        float[] orientation = new float[3];
        SensorManager.getOrientation(R, orientation);

        float azimute = (float) Math.toDegrees(orientation[0]);
        azimute = (azimute + 360) % 360;

        listener.onAzimuteChanged(azimute);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}