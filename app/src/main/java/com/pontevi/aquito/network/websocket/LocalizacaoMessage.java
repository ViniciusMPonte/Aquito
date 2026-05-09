package com.pontevi.aquito.network.websocket;

public class LocalizacaoMessage extends SalaMessage {
    public double latitude;
    public double longitude;

    public LocalizacaoMessage() {}

    public LocalizacaoMessage(String chave, double latitude, double longitude) {
        super(chave);
        this.latitude = latitude;
        this.longitude = longitude;
    }
}