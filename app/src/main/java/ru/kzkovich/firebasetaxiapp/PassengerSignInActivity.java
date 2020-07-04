package ru.kzkovich.firebasetaxiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PassengerSignInActivity extends AppCompatActivity {

    private static final String TAG = "PassengerSignInActivity";

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputName;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private Button loginSignUpButton;
    private TextView toggleSignUpTextView;
    private boolean isLoginModeActive;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_sign_in);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent (PassengerSignInActivity.this, PassengerMapsActivity.class));
        }

        textInputEmail = findViewById(R.id.textInputEmail);
        textInputName = findViewById(R.id.textInputName);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        toggleSignUpTextView = findViewById(R.id.toggleSignUpTextView);
    }

    private boolean isEmailValid() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()){
            textInputEmail.setError("Введите адрес эл.почты");
            return true;
        } else {
            textInputEmail.setError("");
            return false;
        }
    }

    private boolean isNameValid() {
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if (nameInput.isEmpty()){
            textInputName.setError("Введите имя");
            return true;
        } else if (nameInput.length() > 15){
            textInputName.setError("Имя не должно быть больше 15 символов");
            return true;
        } else {
            textInputName.setError("");
            return false;
        }
    }

    private boolean isPasswordValid() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()){
            textInputPassword.setError("Введите пароль");
            return true;
        } else if (passwordInput.length() < 8){
            textInputPassword.setError("Длина пароля должна быть 8 или больше символов");
            return true;
        } else {
            textInputPassword.setError("");
            textInputConfirmPassword.setError("");
            return false;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String confirmPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();

        if (!passwordInput.equals(confirmPasswordInput)) {
            textInputPassword.setError("Пароли не совпадают");
            return false;
        } else if (confirmPasswordInput.isEmpty()){
            textInputConfirmPassword.setError("Подтвердите пароль");
            return false;
        } else {
            textInputPassword.setError("");
            textInputConfirmPassword.setError("");
            return true;
        }
    }

    public void toggleSignUp (View view) {
        if (isLoginModeActive) {
            isLoginModeActive = false;
            loginSignUpButton.setText("Зарегистрироваться");
            toggleSignUpTextView.setText("нажмите сюда для авторизации");
            textInputConfirmPassword.setVisibility(View.VISIBLE);
        } else {
            isLoginModeActive = true;
            loginSignUpButton.setText("Авторизоваться");
            toggleSignUpTextView.setText("нажмите сюда для регистрации");
            textInputConfirmPassword.setVisibility(View.GONE);
        }
    }

    public void fbSignInUser () {
        String password = textInputPassword.getEditText().getText().toString().trim();
        String email = textInputEmail.getEditText().getText().toString().trim();

        if (isNameValid() | isEmailValid() | isPasswordValid()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(PassengerSignInActivity.this,
                                    PassengerMapsActivity.class));
//                                updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(PassengerSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void fbSignUpUser() {
        String password = textInputPassword.getEditText().getText().toString().trim();
        String email = textInputEmail.getEditText().getText().toString().trim();

        if (isNameValid() | isEmailValid() | isPasswordValid() | !validateConfirmPassword()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(PassengerSignInActivity.this,
                                    PassengerMapsActivity.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PassengerSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void loginSignUpUser (View view) {

        if (isLoginModeActive) {
            fbSignInUser();
        } else {
            fbSignUpUser();
        }
    }
}
