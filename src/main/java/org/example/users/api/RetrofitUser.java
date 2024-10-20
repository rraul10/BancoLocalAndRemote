package org.example.users.api;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;

@Singleton
public class RetrofitUser {

    private static Retrofit retrofit = null;

    public static Retrofit getUser(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
