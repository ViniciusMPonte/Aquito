package com.pontevi.aquito.ui.bussola;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pontevi.aquito.BussolaViewModel;
import com.pontevi.aquito.R;
import com.pontevi.aquito.localizacao.GeoUtils;
import com.pontevi.aquito.localizacao.LocalizacaoClient;
import com.pontevi.aquito.network.websocket.LocalizacaoMessage;
import com.pontevi.aquito.network.websocket.localizacao.LocalizacaoApiClient;
import com.pontevi.aquito.network.websocket.localizacao.render.RenderLocalizacaoApiClient;

import java.util.LinkedHashSet;
import java.util.Set;

public class BussolaFragment extends Fragment {

    private static final String TAG = "WebSocket";

    private BussolaViewModel viewModel;
    private BussolaView bussolaView;
    private LocalizacaoApiClient localizacaoApiClient;
    private LocalizacaoClient localizacaoClient;

    private TextView txtStatusConexao;
    private TextView txtSala;
    private TextView txtMembros;
    private TextView txtEventos;
    private TextView txtUltimaLocalizacao;
    private EditText inputApelido;
    private EditText inputChave;

    private final Set<String> membros = new LinkedHashSet<>();

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
        txtStatusConexao = view.findViewById(R.id.txt_status_conexao);
        txtSala = view.findViewById(R.id.txt_sala);
        txtMembros = view.findViewById(R.id.txt_membros);
        txtEventos = view.findViewById(R.id.txt_eventos);
        txtUltimaLocalizacao = view.findViewById(R.id.txt_ultima_localizacao);
        inputApelido = view.findViewById(R.id.input_apelido);
        inputChave = view.findViewById(R.id.input_chave);

        viewModel = new ViewModelProvider(requireActivity()).get(BussolaViewModel.class);
        localizacaoClient = new LocalizacaoClient(requireContext());

        viewModel.getAzimute().observe(getViewLifecycleOwner(), bussolaView::setAzimute);

        view.findViewById(R.id.btn_entrar).setOnClickListener(v -> {
            String chave = inputChave.getText().toString().trim();
            String apelido = inputApelido.getText().toString().trim();
            if (chave.isEmpty() || apelido.isEmpty()) return;
            membros.clear();
            membros.add(apelido);
            atualizarSala(chave);
            atualizarMembros();
            localizacaoApiClient.entrar(chave, apelido);
            adicionarEvento("Você entrou na sala.");
        });

        view.findViewById(R.id.btn_sair).setOnClickListener(v -> {
            localizacaoApiClient.sair();
            membros.clear();
            txtSala.setText("Sala: —");
            atualizarMembros();
            adicionarEvento("Você saiu da sala.");
        });

        view.findViewById(R.id.btn_enviar_localizacao).setOnClickListener(v -> {
            localizacaoClient.obterLocalizacao()
                    .thenAccept(location -> localizacaoApiClient.enviarLocalizacao(
                            location.getLatitude(),
                            location.getLongitude()
                    ))
                    .exceptionally(erro -> {
                        Log.e(TAG, "Erro ao obter localização: " + erro.getMessage());
                        return null;
                    });
        });

        localizacaoApiClient = RenderLocalizacaoApiClient.criar();
        localizacaoApiClient.conectar(new LocalizacaoApiClient.Listener() {
            @Override
            public void onConectado() {
                Log.d(TAG, "Conectado!");
                requireActivity().runOnUiThread(() ->
                        txtStatusConexao.setText("Status: conectado"));
            }

            @Override
            public void onDesconectado() {
                Log.d(TAG, "Desconectado.");
                requireActivity().runOnUiThread(() ->
                        txtStatusConexao.setText("Status: desconectado"));
            }

            @Override
            public void onErro(Throwable erro) {
                Log.e(TAG, "Erro: " + erro.getMessage());
                requireActivity().runOnUiThread(() ->
                        txtStatusConexao.setText("Status: erro — " + erro.getMessage()));
            }

            @Override
            public void onAlguemEntrou(String apelido) {
                Log.d(TAG, apelido + " entrou na sala.");
                requireActivity().runOnUiThread(() -> {
                    membros.add(apelido);
                    atualizarMembros();
                    adicionarEvento(apelido + " entrou na sala.");
                });
            }

            @Override
            public void onAlguemSaiu(String apelido) {
                Log.d(TAG, apelido + " saiu da sala.");
                requireActivity().runOnUiThread(() -> {
                    membros.remove(apelido);
                    atualizarMembros();
                    adicionarEvento(apelido + " saiu da sala.");
                });
            }

            @Override
            public void onLocalizacaoRecebida(LocalizacaoMessage mensagem) {
                Log.d(TAG, "Lat: " + mensagem.latitude + " Lng: " + mensagem.longitude);

                requireActivity().runOnUiThread(() ->
                        txtUltimaLocalizacao.setText(
                                "Última localização recebida: " +
                                        mensagem.latitude + ", " + mensagem.longitude));

                localizacaoClient.obterLocalizacao().thenAccept(minhaLocalizacao -> {
                    float bearing = GeoUtils.calcularBearing(
                            minhaLocalizacao.getLatitude(),
                            minhaLocalizacao.getLongitude(),
                            mensagem.latitude,
                            mensagem.longitude
                    );
                    requireActivity().runOnUiThread(() -> bussolaView.setBearing(bearing));
                });
            }
        });
    }

    private void atualizarSala(String chave) {
        txtSala.setText("Sala: " + chave);
    }

    private void atualizarMembros() {
        txtMembros.setText(membros.isEmpty() ? "Na sala: —" : "Na sala: " + String.join(", ", membros));
    }

    private void adicionarEvento(String evento) {
        String atual = txtEventos.getText().toString();
        txtEventos.setText(atual.equals("—") ? evento : atual + "\n" + evento);
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