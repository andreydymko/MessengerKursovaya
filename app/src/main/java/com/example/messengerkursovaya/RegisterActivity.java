package com.example.messengerkursovaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements
        View.OnClickListener{

    private final static String TAG = "Register Activity";
    private FirebaseAuth mAuth;
    private TextView userEmail, userPassword1, userPassword2;
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.userEmailReg);
        userPassword1 = findViewById(R.id.userPasswordReg1);
        userPassword2 = findViewById(R.id.userPasswordReg2);
        createAccount = findViewById(R.id.button_createAccount);

        createAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_createAccount:
                createNewAccount();
                break;
            default:
                break;
        }
    }

    private void createNewAccount(){
        final String email = userEmail.getText().toString().trim();
        String password1 = userPassword1.getText().toString().trim();
        String password2 = userPassword2.getText().toString().trim();


        if (checkTextFields(email, password1, password2)) {
            createAccount.setClickable(false);
            mAuth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                writeNewUserInfoToDB(email);
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    createAccount.setClickable(true);
                }
            });
        }
    }

    private boolean checkTextFields(String email, String password1, String password2) {
        boolean isEverythingRight = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError(this.getString(R.string.error_email_not_valid));
            isEverythingRight = false;
        }
        if(TextUtils.isEmpty(email)) {
            userEmail.setError(this.getString(R.string.error_empty_field));
            isEverythingRight = false;
        }
        if(!password1.equals(password2)) {
            userPassword1.setError(this.getString(R.string.error_passwords_arent_same));
            userPassword2.setError(this.getString(R.string.error_passwords_arent_same));
            isEverythingRight = false;
        }
        if(password1.length() <= 5) {
            userPassword1.setError(this.getString(R.string.error_password_short));
            isEverythingRight = false;
        }
        if(TextUtils.isEmpty(password1)) {
            userPassword1.setError(this.getString(R.string.error_empty_field));
            isEverythingRight = false;
        }
        if(TextUtils.isEmpty(password2)) {
            userPassword2.setError(this.getString(R.string.error_empty_field));
            isEverythingRight = false;
        }

        return isEverythingRight;
    }

    private void writeNewUserInfoToDB(final String newUserId) {
        Map<String, Object> newUserData = new HashMap<>();
        // 1 year since epoch
        // because of Timezones
        newUserData.put("blockedUntil", new Date(31536000L*1000));
        newUserData.put("dialogs", Collections.emptyList());
        newUserData.put("isAdmin", false);
        newUserData.put("isBlocked", false);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(newUserId)
                .set(newUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SendUserToLoginActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete();
                        }
                        Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void SendUserToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

}
