package hvcnbcvt_uddd.googleapi.View;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.View.Login.LoginFragment;
import hvcnbcvt_uddd.googleapi.data.database.AppDatabase;
import hvcnbcvt_uddd.googleapi.data.database.AppExecutors;
import hvcnbcvt_uddd.googleapi.data.database.LoginDao;
import hvcnbcvt_uddd.googleapi.data.database.PrefHelper;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserName;
    TextView txtUserEmail;
    TextView txtUserMobile;
    PrefHelper prefHelper;
    AppDatabase appDb;
    LoginDao profileDao;
    AppCompatImageView btnBack;

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
                    User user = profileDao.loadUserByEmail(userEmail);
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
    }

    private void updateUserUI(User user) {
        if (user == null) {
            return;
        }
        txtUserName.setText(user.getName());
        txtUserEmail.setText(user.getEmail());
        txtUserMobile.setText(user.getPhone());
    }

    private void initView() {
        txtUserName = findViewById(R.id.txt_user_name);
        txtUserEmail = findViewById(R.id.txt_user_email);
        txtUserMobile = findViewById(R.id.txt_user_mobile);
        btnBack = findViewById(R.id.btn_back);

        appDb = AppDatabase.getInstance(this);
        profileDao = appDb.loginDao();

        prefHelper = new PrefHelper(this);

    }
}
