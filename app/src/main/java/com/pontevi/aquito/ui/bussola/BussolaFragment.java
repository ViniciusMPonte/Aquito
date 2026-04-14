package com.pontevi.aquito.ui.bussola;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pontevi.aquito.BussolaViewModel;
import com.pontevi.aquito.R;
import com.pontevi.aquito.network.websocket.LocalizacaoMessage;
import com.pontevi.aquito.network.websocket.localizacao.LocalizacaoApiClient;
import com.pontevi.aquito.network.websocket.localizacao.render.RenderLocalizacaoApiClient;

public class BussolaFragment extends Fragment {

    private static final String TAG = "WebSocket";

    private BussolaViewModel viewModel;
    private BussolaView bussolaView;
    private LocalizacaoApiClient localizacaoApiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bussola, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bussolaView = view.findViewById(R.id.bussola_view);
        viewModel = new ViewModelProvider(requireActivity()).get(BussolaViewModel.class);

        viewModel.getAzimute().observe(getViewLifecycleOwner(), azimute -> {
            bussolaView.setAzimute(azimute);
        });

        localizacaoApiClient = RenderLocalizacaoApiClient.criar();
        localizacaoApiClient.conectar(new LocalizacaoApiClient.Listener() {
            @Override
            public void onConectado() {
                Log.d(TAG, "Conectado!");
            }

            @Override
            public void onLocalizacaoRecebida(LocalizacaoMessage mensagem) {
                Log.d(TAG, "Lat: " + mensagem.latitude + " Lng: " + mensagem.longitude);
            }

            @Override
            public void onErro(Throwable erro) {
                Log.e(TAG, "Erro: " + erro.getMessage());
            }

            @Override
            public void onDesconectado() {
                Log.d(TAG, "Desconectado.");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.iniciarSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.pararSensor();
        localizacaoApiClient.desconectar();
    }
}