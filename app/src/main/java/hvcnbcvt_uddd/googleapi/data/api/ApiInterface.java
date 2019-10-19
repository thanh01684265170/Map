package hvcnbcvt_uddd.googleapi.data.api;

import java.util.HashMap;

import hvcnbcvt_uddd.googleapi.Model.BodySendSOS;
import hvcnbcvt_uddd.googleapi.Model.DataSos;
import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.LoginResponse;
import hvcnbcvt_uddd.googleapi.Model.datasosresponse.SosResponse;
import hvcnbcvt_uddd.googleapi.Model.datauserlistresponse.DataUsersSosResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<SosResponse> responseSOS(@Body HashMap<String, String> entityId);

    @POST("cancel")
    Call<DataSos> cancelSOS(@Body HashMap<String, String> entityId);

    @GET("entity/{entityId}")
    Call<DataUsersSosResponse> getUsersSos(@Path("entityId") String entityId);
}
