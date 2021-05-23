package com.example.qfilm.repositories.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 *
 * Used to post values from background threads on main thread
 *
 * **/

public class MainThreadExecutor implements Executor {

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public MainThreadExecutor() {
    }

    @Override
    public void execute(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}