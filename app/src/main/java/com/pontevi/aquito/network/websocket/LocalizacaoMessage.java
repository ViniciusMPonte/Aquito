package com.pontevi.aquito.network.websocket;

public class LocalizacaoMessage {
    public double latitude;
    public double longitude;

    public LocalizacaoMessage(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}