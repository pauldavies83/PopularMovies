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
    protected OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
             okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        }
        return okHttpClient;
    }
}