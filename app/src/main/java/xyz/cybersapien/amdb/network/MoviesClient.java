package xyz.cybersapien.amdb.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ogcybersapien on 9/11/17.
 */

public class MoviesClient {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit retrofitClient = null;

    public static Retrofit getClient() {
        if (retrofitClient == null)
            retrofitClient = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofitClient;
    }

    private static OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
    }
}
