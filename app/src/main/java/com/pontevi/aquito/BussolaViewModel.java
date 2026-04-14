package com.pontevi.aquito;

import android.app.Application;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pontevi.aquito.sensor.BussolaSensor;

public class BussolaViewModel extends AndroidViewModel {

    private final BussolaSensor bussolaSensor;
    private final MutableLiveData<Float> azimute = new MutableLiveData<>();

    public BussolaViewModel(@NonNull Application application) {
        super(application);

        SensorManager sensorManager =
                (SensorManager) application.getSystemService(Application.SENSOR_SERVICE);

        bussolaSensor = new BussolaSensor(sensorManager, azimute::setValue);
    }

    public LiveData<Float> getAzimute() {
        return azimute;
    }

    public void iniciarSensor() {
        bussolaSensor.iniciar();
    }

    public void pararSensor() {
        bussolaSensor.parar();
    }
}