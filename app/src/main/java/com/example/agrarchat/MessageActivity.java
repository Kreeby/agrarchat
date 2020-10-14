package com.example.agrarchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrarchat.Adapter.MessageAdapter;
import com.example.agrarchat.Model.Chat;
import com.example.agrarchat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference reference;

    ImageButton sendMessage;
    EditText messageText;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;


    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profileImage = findViewById(R.id.circleImageView);
        username = findViewById(R.id.username);
        sendMessage = findViewById(R.id.send);
        messageText = findViewById(R.id.text_send);


        intent = getIntent();
        final String userId = intent.getStringExtra("userId");

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageText.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Boş mesaj göndərə bilməssiniz", Toast.LENGTH_SHORT).show();
                }
                messageText.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getFullname());
                if (user.getImageURL().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profileImage);
                }

                readMessage(fUser.getUid(), userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

    }


    private void readMessage(final String myId, final String userId) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 mChat.clear();
                 for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                     Chat chat = dataSnapshot.getValue(Chat.class);
                     if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                             chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                         mChat.add(chat);
                     }

                     messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                     recyclerView.setAdapter(messageAdapter);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
