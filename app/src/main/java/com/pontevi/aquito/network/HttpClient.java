package com.pontevi.aquito.network;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class HttpClient {

    public static final Handler mainThread = new Handler(Looper.getMainLooper());

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    public static OkHttpClient getInstance() {
        return okHttpClient;
    }
}