package com.example.agrarchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.agrarchat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference reference;

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

         profileImage = findViewById(R.id.circleImageView);
         username = findViewById(R.id.username);

         intent = getIntent();
         final String userId = intent.getStringExtra("userId");

         fUser = FirebaseAuth.getInstance().getCurrentUser();
         reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);


         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 User user = snapshot.getValue(User.class);
                 username.setText(user.getFullname());
                 if(user.getImageURL().equals("default")) {
                     profileImage.setImageResource(R.mipmap.ic_launcher);
                 }
                 else {
                     Glide.with(MessageActivity.this).load(user.getImageURL()).into(profileImage);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

    }
}
