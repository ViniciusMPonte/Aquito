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

public class BussolaFragment extends Fragment {

    private static final String TAG = "Bussola";
    private BussolaViewModel viewModel;

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

        viewModel = new ViewModelProvider(requireActivity()).get(BussolaViewModel.class);

        viewModel.getAzimute().observe(getViewLifecycleOwner(), azimute -> {
            Log.d(TAG, "Azimute: " + azimute);
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
    }
}