package com.example.quality_of_air_monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.quality_of_air_monitoring.accounts_creation.DatabaseHelper;
import com.example.quality_of_air_monitoring.accounts_creation.InputValidation;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    private LinearLayout root_layout;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        root_layout = (LinearLayout) view.findViewById(R.id.rel_root);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.et_email);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.et_password);
        textInputEditTextEmail = (TextInputEditText) view.findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) view.findViewById(R.id.textInputEditTextPassword);

        // Initialize used objects
        databaseHelper = new DatabaseHelper(getContext());
        inputValidation = new InputValidation(getContext());

        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /**
                 * FOR TESTING ONLY
                 * UNCOMMENT verifyAccount() and comment the rest for real app
                 */
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                //verifyAccount();
            }
        });
        return view;
    }
    
    /************************************
    * Input validation for login        *
     * Verify if input filled           *
     * Verify if valid email            *
     * Verify if email-password exists  *
     ***********************************/
    private void verifyAccount() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }
        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(intent);
        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(root_layout, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Method to empty all inputs
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

}
