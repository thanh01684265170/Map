package hvcnbcvt_uddd.googleapi.View.Login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.LoginResponse;
import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.View.MapsActivity;
import hvcnbcvt_uddd.googleapi.data.api.ApiBuilder;
import hvcnbcvt_uddd.googleapi.data.api.ApiInterface;
import hvcnbcvt_uddd.googleapi.data.database.AppDatabase;
import hvcnbcvt_uddd.googleapi.data.database.LoginDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static LoginFragment instance = null;

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView txtSignup;
    ApiInterface apiInterface;
    AppDatabase appDb;
    LoginDao loginDao;

    public static LoginFragment newInstance() {
        if (instance == null) {
            instance = new LoginFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        handleEvent();
    }

    private void handleEvent() {
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment, SignupFragment.newInstance(),
                                SignupFragment.class.getSimpleName())
                        .addToBackStack(SignupFragment.class.getSimpleName()).commit();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        loginRequest(progressDialog);
    }

    private void initView(View view) {
        editEmail = view.findViewById(R.id.input_email);
        editPassword = view.findViewById(R.id.input_password);
        btnLogin = view.findViewById(R.id.btn_login);
        txtSignup = view.findViewById(R.id.link_signup);

        apiInterface = ApiBuilder.getServiceApi();

        appDb = AppDatabase.getDatabase(getContext());
        loginDao = appDb.loginDao();
    }

    private void loginRequest(final ProgressDialog progressDialog) {
        HashMap<String, String> option = new HashMap<>();
        option.put("email", editEmail.getText().toString());
        option.put("password", editPassword.getText().toString());

        Call call = apiInterface.login(option);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Chinh", "onResponse");
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = (LoginResponse) response.body();
                    handleLoginRequest(loginResponse);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("Chinh", "onFailure: " + t.toString());
                onLoginFailed();
                progressDialog.dismiss();
            }
        });
    }

    private void handleLoginRequest(LoginResponse loginResponse) {
//        String a = loginResponse.getMessage();
//        Toast.makeText(getContext(), a, Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);

        if (loginResponse.getMessage().equals("Sign in successfully")) {
            User user_login = loginResponse.getData().getUser();
            new insertAsyncTask(loginDao).execute(user_login);

            Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();

            Intent mapsActivityIntent = new Intent(getContext(), MapsActivity.class);
            startActivity(mapsActivityIntent);
        } else {
            Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
        }
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

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        //transition do orther activity.
    }

    public void onLoginFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = editEmail.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("enter a valid email address");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        return valid;
    }
}
