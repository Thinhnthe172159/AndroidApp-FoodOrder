package com.fu.thinh_nguyen.qrfoodorder.data.network;
import static com.fu.thinh_nguyen.qrfoodorder.data.config.Constants.BASE_URL;

import com.fu.thinh_nguyen.qrfoodorder.data.api.AuthInterceptor;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {

    private static Retrofit retrofit;
    public static Retrofit getInstance(TokenManager tm) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tm))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
