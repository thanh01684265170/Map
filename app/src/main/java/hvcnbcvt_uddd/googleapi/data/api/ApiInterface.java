package hvcnbcvt_uddd.googleapi.data.api;

import java.util.HashMap;

import hvcnbcvt_uddd.googleapi.Model.BodySendSOS;
import hvcnbcvt_uddd.googleapi.Model.DataSos;
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
    Call<DataSos> requestSOS(@Body HashMap<String, Double> parameters);

    @POST("request")
    Call<DataSos> sendSOS(@Body BodySendSOS bodySendSOS);

    @POST("response")
    Call<LoginResponse> responseSOS(@Body HashMap<String, String> entityId, @Body HashMap<String, Boolean> accept);

    @POST("cancel")
    Call<DataSos> cancelSOS(@Body HashMap<String, String> entityId);
}