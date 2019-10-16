package hvcnbcvt_uddd.googleapi.data.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import hvcnbcvt_uddd.googleapi.data.database.PrefHelper;
import hvcnbcvt_uddd.googleapi.utils.ApiUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {

    public static String AUTHORIZATION_KEY = "AUTHORIZATION_KEY";

    public static ApiInterface getServiceApi(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient.Builder builder = httpClient.addInterceptor(interceptor);

        final PrefHelper prefHelper = new PrefHelper(context);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                String author = "Bearer " + prefHelper.getString("AUTHORIZATION_KEY");
                Log.d("ApiBuilder", "intercept:" + author);
                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", author)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }
}
