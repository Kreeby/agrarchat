package com.example.agrarchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText mail, password;
    Button login;
    TextView goToRegister;
    Button showPass;
    ImageView showPassImage;
    FirebaseAuth auth;
    boolean isPressed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();

        goToRegister = findViewById(R.id.goToRegister);
        showPass = findViewById(R.id.showPass);
        showPassImage = findViewById(R.id.showPassImage);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isPressed = !isPressed;
                if (isPressed) {
                    password.setTransformationMethod(null);
                    showPassImage.setBackgroundResource(R.drawable.ic_eye_off);
                } else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    showPassImage.setBackgroundResource(R.drawable.ic_eye);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMail = mail.getText().toString();
                String mPassword = password.getText().toString();
                if (mPassword.isEmpty() || mPassword.length() < 6) {
                    password.setError("Parol minimum 6 ibaret olmalidir");
                    password.requestFocus();
                    return;
                }

                auth.signInWithEmailAndPassword(mMail, mPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Nese elebil netodu..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}
