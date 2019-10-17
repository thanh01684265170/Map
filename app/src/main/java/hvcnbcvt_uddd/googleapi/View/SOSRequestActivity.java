package hvcnbcvt_uddd.googleapi.View;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import hvcnbcvt_uddd.googleapi.R;

public class SOSRequestActivity extends AppCompatActivity implements View.OnClickListener {


    private AppCompatImageView buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosrequest);
        initView();
        setEvent();
    }

    private void initView() {
        buttonBack = findViewById(R.id.btn_back);
    }

    private void setEvent() {
        buttonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
