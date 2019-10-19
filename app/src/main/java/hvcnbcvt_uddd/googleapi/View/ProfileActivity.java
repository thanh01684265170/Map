package hvcnbcvt_uddd.googleapi.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

import java.util.HashMap;

import hvcnbcvt_uddd.googleapi.Model.datafreeresponse.DataFreeResponse;
import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.View.Login.LoginFragment;
import hvcnbcvt_uddd.googleapi.data.api.ApiBuilder;
import hvcnbcvt_uddd.googleapi.data.api.ApiInterface;
import hvcnbcvt_uddd.googleapi.data.database.AppDatabase;
import hvcnbcvt_uddd.googleapi.data.database.AppExecutors;
import hvcnbcvt_uddd.googleapi.data.database.LoginDao;
import hvcnbcvt_uddd.googleapi.data.database.PrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String STATUS_FREE = "1";
    private static final String STATUS_NOT_FREE = "0";

    TextView txtUserName;
    TextView txtUserEmail;
    TextView txtUserMobile;
    PrefHelper prefHelper;
    AppDatabase appDb;
    LoginDao profileDao;
    AppCompatImageView btnBack;
    SwitchCompat switchFree;
    User user;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        handleEvents();
    }

    private void handleEvents() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String userEmail = prefHelper.getString(LoginFragment.USER_EMAIL);
                if (!userEmail.isEmpty()) {
                    user = profileDao.loadUserByEmail(userEmail);
                    updateUserUI(user);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        switchFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchFree.isChecked()) {
                    setFreeRequest(STATUS_FREE);
                } else {
                    setFreeRequest(STATUS_NOT_FREE);
                }
            }
        });
    }

    private void setFreeRequest(final String statusFree) {
        HashMap<String, String> option = new HashMap<>();
        option.put("free", statusFree);
        apiInterface.sendDataFreeRequest(option).enqueue(new Callback<DataFreeResponse>() {
            @Override
            public void onResponse(Call<DataFreeResponse> call, Response<DataFreeResponse> response) {
                if (response.isSuccessful()) {
                    String msg = response.body().getMessage();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    if (user != null) {
                        user.setFree(Integer.parseInt(statusFree));
                        new insertAsyncTask(profileDao).execute(user);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataFreeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Request fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private LoginDao mAsyncTaskDao;

        insertAsyncTask(LoginDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.saveUserLogin(users[0]);
            return null;
        }
    }

    private void updateUserUI(User user) {
        if (user == null) {
            return;
        }
        txtUserName.setText(user.getName());
        txtUserEmail.setText(user.getEmail());
        txtUserMobile.setText(user.getPhone());

        if (user.getFree() == Integer.parseInt(STATUS_FREE)) {
            switchFree.setChecked(true);
        } else {
            switchFree.setChecked(false);
        }
    }

    private void initView() {
        txtUserName = findViewById(R.id.txt_user_name);
        txtUserEmail = findViewById(R.id.txt_user_email);
        txtUserMobile = findViewById(R.id.txt_user_mobile);
        btnBack = findViewById(R.id.btn_back);
        switchFree = findViewById(R.id.switch_free);

        appDb = AppDatabase.getInstance(this);
        profileDao = appDb.loginDao();

        prefHelper = new PrefHelper(this);
        apiInterface = ApiBuilder.getServiceApi(this);
    }
}
