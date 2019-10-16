package hvcnbcvt_uddd.googleapi.View.Login;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import hvcnbcvt_uddd.googleapi.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    private static SignupFragment instance = null;

    EditText editName;
    EditText editEmail;
    EditText editPhone;
    EditText editPassword;
    EditText editRePassWord;
    Button btnSignUp;
    TextView txtLogin;
    AppCompatSpinner spinnerGender;

    public static SignupFragment newInstance() {
        if (instance == null) {
            instance = new SignupFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        handleEvents();
    }

    private void handleEvents() {
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment, LoginFragment.newInstance(),
                                LoginFragment.class.getSimpleName())
                        .addToBackStack(LoginFragment.class.getSimpleName()).commit();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        btnSignUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 500);
    }

    public void onSignupSuccess() {
        btnSignUp.setEnabled(true);
        getActivity().setResult(RESULT_OK, null);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment, LoginFragment.newInstance(),
                        LoginFragment.class.getSimpleName())
                .addToBackStack(LoginFragment.class.getSimpleName()).commit();
    }

    public void onSignupFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        btnSignUp.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String mobile = editPhone.getText().toString();
        String password = editPassword.getText().toString();
        String reEnterPassword = editRePassWord.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            editName.setError("at least 3 characters");
            valid = false;
        } else {
            editName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("enter a valid email address");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            editPhone.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            editPhone.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            editPassword.setError("Must have at least 6 characters");
            valid = false;
        } else {
            editPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6|| !(reEnterPassword.equals(password))) {
            editRePassWord.setError("Password Do not match");
            valid = false;
        } else {
            editRePassWord.setError(null);
        }

        return valid;
    }

    private void initView(View view) {
        editName = view.findViewById(R.id.input_name);
        editEmail = view.findViewById(R.id.input_email);
        editPhone = view.findViewById(R.id.input_mobile);
        editPassword = view.findViewById(R.id.input_password);
        editRePassWord = view.findViewById(R.id.input_reEnterPassword);
        btnSignUp = view.findViewById(R.id.btn_signup);
        txtLogin = view.findViewById(R.id.link_login);
        spinnerGender = view.findViewById(R.id.spinner_gender);
    }
}
