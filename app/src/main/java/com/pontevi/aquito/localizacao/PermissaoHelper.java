package com.pontevi.aquito.localizacao;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissaoHelper {

    private static final String[] PERMISSOES_BASICAS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
    };

    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String[]> launcherBasico;
    private final ActivityResultLauncher<String[]> launcherBackground;
    private Runnable onConcedida;

    public PermissaoHelper(AppCompatActivity activity) {
        this.activity = activity;
        this.launcherBasico = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                resultado -> {
                    boolean todasConcedidas = resultado.values()
                            .stream()
                            .allMatch(Boolean::booleanValue);

                    if (todasConcedidas) {
                        solicitarBackground();
                    }
                }
        );

        this.launcherBackground = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                resultado -> {
                    if (onConcedida != null) {
                        onConcedida.run();
                    }
                }
        );
    }

    public void solicitar(Runnable onConcedida) {
        this.onConcedida = onConcedida;

        if (temTodasPermissoes()) {
            onConcedida.run();
        } else {
            launcherBasico.launch(PERMISSOES_BASICAS);
        }
    }

    private void solicitarBackground() {
        if (temPermissaoBackground()) {
            if (onConcedida != null) onConcedida.run();
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Localização em segundo plano")
                .setMessage("selecione \"Permitir o tempo todo\" nas configurações de localização.")
                .setPositiveButton("Abrir configurações", (dialog, which) ->
                        launcherBackground.launch(
                                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}
                        )
                )
                .setNegativeButton("Agora não", (dialog, which) -> {
                    if (onConcedida != null) onConcedida.run();
                })
                .show();
    }

    public boolean temTodasPermissoes() {
        for (String permissao : PERMISSOES_BASICAS) {
            if (ContextCompat.checkSelfPermission(activity, permissao)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean temPermissaoBackground() {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
}