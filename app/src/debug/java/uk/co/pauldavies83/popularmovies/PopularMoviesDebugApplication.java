package uk.co.pauldavies83.popularmovies;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class PopularMoviesDebugApplication extends PopularMoviesApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            super.getOkHttpClient().newBuilder()
                     .addNetworkInterceptor(new StethoInterceptor())
                     .build();
        }
        return okHttpClient;
    }
}