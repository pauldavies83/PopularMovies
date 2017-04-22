package uk.co.pauldavies83.popularmovies;

import android.app.Application;

import okhttp3.OkHttpClient;

public class PopularMoviesApplication extends Application {

    protected OkHttpClient okHttpClient;

    protected OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

}