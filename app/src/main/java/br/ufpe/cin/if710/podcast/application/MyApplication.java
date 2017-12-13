package br.ufpe.cin.if710.podcast.application;

import android.app.Application;

import com.frogermcs.androiddevmetrics.AndroidDevMetrics;

import br.ufpe.cin.if710.podcast.BuildConfig;

/**
 * Created by Rogerio Santos on 13/12/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Use it only in debug builds
        if (BuildConfig.DEBUG) {
            AndroidDevMetrics.initWith(this);
        }
    }
}

