package com.pontevi.aquito.network.websocket.localizacao;

import com.pontevi.aquito.network.websocket.LocalizacaoMessage;

public interface LocalizacaoApiClient {

    interface Listener {
        void onConectado();
        void onDesconectado();
        void onErro(Throwable erro);
        void onAlguemEntrou(String apelido);
        void onAlguemSaiu(String apelido);
        void onLocalizacaoRecebida(LocalizacaoMessage mensagem);
    }

    void conectar(Listener listener);
    void entrar(String chave, String apelido);
    void enviarLocalizacao(double latitude, double longitude);
    void sair();
    void desconectar();
}