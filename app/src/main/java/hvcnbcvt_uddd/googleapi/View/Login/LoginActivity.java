package hvcnbcvt_uddd.googleapi.View.Login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import hvcnbcvt_uddd.googleapi.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment, LoginFragment.newInstance(),
                        LoginFragment.class.getSimpleName())
                .addToBackStack(LoginFragment.class.getSimpleName()).commit();
    }

}
