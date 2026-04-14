package com.pontevi.aquito;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pontevi.aquito.localizacao.PermissaoHelper;
import com.pontevi.aquito.ui.bussola.BussolaFragment;

public class MainActivity extends AppCompatActivity {

    private PermissaoHelper permissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissao = new PermissaoHelper(this);
        permissao.solicitar(() -> {

        });


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new BussolaFragment())
                    .commit();
        }
    }
}