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


import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    EditText name, password;
    EditText mail;
    Button register;
    TextView goToLogin;
    FirebaseAuth auth;
    DatabaseReference reference;

    Button showPass;
    ImageView showPassImage;

    boolean isPressed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        name = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        mail = findViewById(R.id.mail);
        register = findViewById(R.id.register);
        goToLogin = findViewById(R.id.goToLogin);
        auth = FirebaseAuth.getInstance();

        showPass = findViewById(R.id.showPass);
        showPassImage = findViewById(R.id.showPassImage);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isPressed=!isPressed;
                if(isPressed) {
                    password.setTransformationMethod(null);
                    showPassImage.setBackgroundResource(R.drawable.ic_eye_off);
                }
                else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    showPassImage.setBackgroundResource(R.drawable.ic_eye);
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPass = password.getText().toString();
                String mName = name.getText().toString();
                String mEmail = mail.getText().toString();

                if(mPass.isEmpty() || mPass.length() < 6) {
                    password.setError("Parol minimum 6 ibaret olmalidir");
                    password.requestFocus();
                    return;
                }
                registration(mPass, mEmail, mName);
            }
        });

    }

    private void registration(String pass, String mail, final String fullname) {
        auth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("fullname", fullname);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });


                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Siz bu email addressnen registrasiyadan keche bilmirsiz...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private String generateHash(String pass) {
        String hash = new String(Hex.encodeHex(DigestUtils.sha256(pass)));
        System.out.println(hash + " HASHED");
        return hash;
    }


}
