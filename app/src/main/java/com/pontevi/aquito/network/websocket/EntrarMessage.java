package com.pontevi.aquito.network.websocket;

public class EntrarMessage extends SalaMessage {
    public String apelido;
    public String uuid;

    public EntrarMessage() {}

    public EntrarMessage(String chave, String apelido, String uuid) {
        super(chave);
        this.apelido = apelido;
        this.uuid = uuid;
    }
}