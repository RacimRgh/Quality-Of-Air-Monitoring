package com.example.quality_of_air_monitoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.quality_of_air_monitoring.accounts_creation.DatabaseHelper;
import com.example.quality_of_air_monitoring.accounts_creation.InputValidation;
import com.example.quality_of_air_monitoring.accounts_creation.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;

    private LinearLayout root_layout;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize views
        root_layout = (LinearLayout) view.findViewById(R.id.reg_root);
        textInputLayoutName = (TextInputLayout) view.findViewById(R.id.registerName);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.registerEmail);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.registerPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) view.findViewById(R.id.registerConfirmPassword);

        textInputEditTextName = (TextInputEditText) view.findViewById(R.id.registerNameEditText);
        textInputEditTextEmail = (TextInputEditText) view.findViewById(R.id.registerEmailEditText);
        textInputEditTextPassword = (TextInputEditText) view.findViewById(R.id.registerPasswordEditText);
        textInputEditTextConfirmPassword = (TextInputEditText) view.findViewById(R.id.registerConfirmPasswordEditText);

        // Initialize used objects
        user = new User();
        databaseHelper = new DatabaseHelper(getContext());
        inputValidation = new InputValidation(getContext());

        Button btnRegister = (Button) view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postDataToSQLite();
                //Intent intent = new Intent(getActivity(), MainActivity.class);
                //startActivity(intent);
            }
        });
        return view;
    }

    /*********************************************
     * Input validation for registering          *
     * Verify if inputs filled                   *
     * Verify if valid email                     *
     * Verify if email-password already exists   *
     * Verify if password and confirmation match *
     ********************************************/

    private void postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }
        if (!databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim())) {
            user.setName(textInputEditTextName.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setPassword(textInputEditTextPassword.getText().toString().trim());
            databaseHelper.addUser(user);
            // Snack Bar to show success message that record saved successfully
            Snackbar.make(root_layout, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(root_layout, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }
    }
        /**
         * Method to empty all inputs
         */
    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
    }

}
