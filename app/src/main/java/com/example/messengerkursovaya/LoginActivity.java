package com.example.messengerkursovaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private final static String TAG = "Login Activity";
    private FirebaseAuth mAuth;
    private TextView userEmail, userPassword;
    private Button logIn, createNewAccount;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userEmail = findViewById(R.id.userEmailReg);
        userPassword = findViewById(R.id.userPasswordReg1);
        logIn = findViewById(R.id.button_logIn);
        createNewAccount = findViewById(R.id.createNewAccount);

        logIn.setOnClickListener(this);
        createNewAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_logIn:
                LogIn();
                break;
            case R.id.createNewAccount:
                //Toast.makeText(this, "Create", Toast.LENGTH_LONG).show();
                SendUserToRegisterActivity();
                break;
            default:
                break;
        }
    }

    private void LogIn(){
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (checkTextFields(email, password)) {
            logIn.setClickable(false);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                //Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    logIn.setClickable(true);
                }
            });
        }
    }

    private boolean checkTextFields(String email, String password) {
        boolean isEverythingRight = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError(this.getString(R.string.error_email_not_valid));
            isEverythingRight = false;
        }
        if(TextUtils.isEmpty(email)) {
            userEmail.setError(this.getString(R.string.error_empty_field));
            isEverythingRight = false;
        }
        if(TextUtils.isEmpty(password)) {
            userPassword.setError(this.getString(R.string.error_empty_field));
            isEverythingRight = false;
        }

        return isEverythingRight;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != null)
            SendUserToMainActivity();
    }

    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
    private void SendUserToRegisterActivity(){
        Intent regIntent = new Intent(this, RegisterActivity.class);
        startActivity(regIntent);
    }

}

