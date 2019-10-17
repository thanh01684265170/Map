package hvcnbcvt_uddd.googleapi.data.api;

import java.util.HashMap;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("sign-in")
    Call<LoginResponse> login(@Body HashMap<String, String> parameters);

    @POST("device-token")
    Call<LoginResponse> sendToken(@Body HashMap<String, String> token);

    @POST("request")
    Call<LoginResponse> requestSOS();
}