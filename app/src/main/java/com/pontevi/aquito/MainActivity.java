package com.pontevi.aquito;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pontevi.aquito.ui.bussola.BussolaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new BussolaFragment())
                    .commit();
        }
    }
}