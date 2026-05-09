package com.pontevi.aquito.network.websocket.localizacao.render;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pontevi.aquito.network.HttpClient;
import com.pontevi.aquito.network.websocket.EntrarMessage;
import com.pontevi.aquito.network.websocket.LocalizacaoMessage;
import com.pontevi.aquito.network.websocket.SalaMessage;
import com.pontevi.aquito.network.websocket.localizacao.LocalizacaoApiClient;

import io.reactivex.disposables.CompositeDisposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class RenderLocalizacaoApiClient implements LocalizacaoApiClient {

    private static final String TAG = "WebSocket";

    private final Gson gson = new Gson();
    private StompClient stompClient;
    private CompositeDisposable disposables;
    private Listener listener;
    private String chaveAtual;

    private RenderLocalizacaoApiClient() {}

    public static RenderLocalizacaoApiClient criar() {
        return new RenderLocalizacaoApiClient();
    }

    @Override
    public void conectar(Listener listener) {
        this.listener = listener;
        disposables = new CompositeDisposable();

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                RenderLocalizacaoApi.URL,
                null,
                HttpClient.getInstance()
        );

        disposables.add(stompClient.lifecycle().subscribe(event -> {
            if (event.getType() == LifecycleEvent.Type.OPENED) {
                listener.onConectado();
            } else if (event.getType() == LifecycleEvent.Type.ERROR) {
                listener.onErro(event.getException());
            } else if (event.getType() == LifecycleEvent.Type.CLOSED) {
                listener.onDesconectado();
            }
        }, erro -> Log.e(TAG, "Erro no lifecycle: " + erro.getMessage())));

        stompClient.connect();
    }

    @Override
    public void entrar(String chave, String apelido) {
        if (stompClient == null || !stompClient.isConnected()) return;
        chaveAtual = chave;

        disposables.add(stompClient.topic(RenderLocalizacaoApi.TOPICO_SALA + chave).subscribe(frame -> {
            JsonObject json = JsonParser.parseString(frame.getPayload()).getAsJsonObject();
            String evento = json.get("evento").getAsString();

            switch (evento) {
                case "ENTROU":
                    listener.onAlguemEntrou(json.get("apelido").getAsString());
                    break;
                case "SAIU":
                    listener.onAlguemSaiu(json.get("apelido").getAsString());
                    break;
                case "LOCALIZACAO":
                    LocalizacaoMessage mensagem = gson.fromJson(json, LocalizacaoMessage.class);
                    listener.onLocalizacaoRecebida(mensagem);
                    break;
                default:
                    Log.w(TAG, "Evento desconhecido: " + evento);
            }
        }, erro -> Log.e(TAG, "Erro no tópico: " + erro.getMessage())));

        String json = gson.toJson(new EntrarMessage(chave, apelido));
        disposables.add(
                stompClient.send(RenderLocalizacaoApi.DESTINO_ENTRAR, json).subscribe(
                        () -> Log.d(TAG, "Entrou na sala: " + chave),
                        erro -> Log.e(TAG, "Erro ao entrar: " + erro.getMessage())
                )
        );
    }

    @Override
    public void enviarLocalizacao(double latitude, double longitude) {
        if (stompClient == null || !stompClient.isConnected() || chaveAtual == null) return;
        String json = gson.toJson(new LocalizacaoMessage(chaveAtual, latitude, longitude));
        disposables.add(
                stompClient.send(RenderLocalizacaoApi.DESTINO_LOCALIZACAO, json).subscribe(
                        () -> Log.d(TAG, "Localização enviada"),
                        erro -> Log.e(TAG, "Erro ao enviar: " + erro.getMessage())
                )
        );
    }

    @Override
    public void sair() {
        if (stompClient == null || !stompClient.isConnected() || chaveAtual == null) return;
        String json = gson.toJson(new SalaMessage(chaveAtual));
        disposables.add(
                stompClient.send(RenderLocalizacaoApi.DESTINO_SAIR, json).subscribe(
                        () -> Log.d(TAG, "Saiu da sala: " + chaveAtual),
                        erro -> Log.e(TAG, "Erro ao sair: " + erro.getMessage())
                )
        );
        chaveAtual = null;
    }

    @Override
    public void desconectar() {
        if (disposables != null) disposables.dispose();
        if (stompClient != null) stompClient.disconnect();
        chaveAtual = null;
    }
}