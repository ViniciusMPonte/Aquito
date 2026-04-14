package com.pontevi.aquito.network.websocket.localizacao.render;

import android.util.Log;

import com.google.gson.Gson;
import com.pontevi.aquito.network.HttpClient;
import com.pontevi.aquito.network.websocket.LocalizacaoMessage;
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

    private RenderLocalizacaoApiClient() {}

    public static RenderLocalizacaoApiClient criar() {
        return new RenderLocalizacaoApiClient();
    }

    @Override
    public void conectar(Listener listener) {
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

        disposables.add(stompClient.topic(RenderLocalizacaoApi.TOPICO).subscribe(frame -> {
            LocalizacaoMessage mensagem = gson.fromJson(frame.getPayload(), LocalizacaoMessage.class);
            listener.onLocalizacaoRecebida(mensagem);
        }, erro -> Log.e(TAG, "Erro no tópico: " + erro.getMessage())));

        stompClient.connect();
    }

    @Override
    public void enviarLocalizacao(double latitude, double longitude) {
        if (stompClient == null || !stompClient.isConnected()) return;
        String json = gson.toJson(new LocalizacaoMessage(latitude, longitude));
        disposables.add(
                stompClient.send(RenderLocalizacaoApi.DESTINO, json).subscribe(
                        () -> Log.d(TAG, "Localização enviada"),
                        erro -> Log.e(TAG, "Erro ao enviar: " + erro.getMessage())
                )
        );
    }

    @Override
    public void desconectar() {
        if (disposables != null) disposables.dispose();
        if (stompClient != null) stompClient.disconnect();
    }
}