package hvcnbcvt_uddd.googleapi.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import hvcnbcvt_uddd.googleapi.Model.BodySendSOS;
import hvcnbcvt_uddd.googleapi.Model.DataSos;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.data.api.ApiBuilder;
import hvcnbcvt_uddd.googleapi.data.api.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SOSRequestActivity extends AppCompatActivity implements View.OnClickListener {


    private AppCompatImageView buttonBack;
    private AppCompatButton buttonSend;
    private AppCompatEditText edtContent;
    private Double lat = 21.0;
    private Double lon = 105.0;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosrequest);
        lat =  getIntent().getDoubleExtra("lat", 12.0);
        lon = getIntent().getDoubleExtra("lon", 105.0);
        initView();
        setEvent();
    }

    private void initView() {
        buttonBack = findViewById(R.id.btn_back);
        buttonSend = findViewById(R.id.button_send);
        apiInterface = ApiBuilder.getServiceApi(this);
        edtContent = findViewById(R.id.edit_content);
    }

    private void setEvent() {
        buttonBack.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.button_send:
                sendSos();
                break;
            default:
                break;
        }
    }

    private void sendSos() {


        BodySendSOS bodySendSOS = new BodySendSOS(lat, lon,  edtContent.getText().toString());

        apiInterface.sendSOS(bodySendSOS).enqueue(new Callback<DataSos>() {
            @Override
            public void onResponse(Call<DataSos> call, Response<DataSos> response) {
                Log.d("SOSRequestActivityyy", "onResponse: ");
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DataSos> call, Throwable t) {
                Log.d("SOSRequestActivityyy", "onFailure: ");
            }
        });
    }
}
