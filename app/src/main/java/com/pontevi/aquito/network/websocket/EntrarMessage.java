package com.pontevi.aquito.network.websocket;

public class EntrarMessage extends SalaMessage {
    public String apelido;

    public EntrarMessage() {}

    public EntrarMessage(String chave, String apelido) {
        super(chave);
        this.apelido = apelido;
    }
}