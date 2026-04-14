package com.pontevi.aquito.network.websocket.localizacao;

import com.pontevi.aquito.network.websocket.LocalizacaoMessage;

public interface LocalizacaoApiClient {

    interface Listener {
        void onConectado();
        void onLocalizacaoRecebida(LocalizacaoMessage mensagem);
        void onErro(Throwable erro);
        void onDesconectado();
    }

    void conectar(Listener listener);
    void enviarLocalizacao(double latitude, double longitude);
    void desconectar();
}