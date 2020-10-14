package com.example.agrarchat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agrarchat.Adapter.UsersAdapter;
import com.example.agrarchat.Model.Chat;
import com.example.agrarchat.Model.User;
import com.example.agrarchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    CircleImageView circleImageView;
    TextView username;
    TextView editProfile;

    FirebaseUser fUser;
    DatabaseReference reference;
    DatabaseReference reference1;

    private List<User> mUsers;
    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        circleImageView = view.findViewById(R.id.circleImageView);
        username = view.findViewById(R.id.username);
        editProfile = view.findViewById(R.id.editProfile);

        usersList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getFullname());
                if(user.getImageURL().equals("default")) {
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getContext()).load(user.getImageURL()).into(circleImageView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sortChats();

        return view;
    }

    private void sortChats() {
        reference1 = FirebaseDatabase.getInstance().getReference("Chats");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getSender().equals(fUser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(fUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void readChats() {
        mUsers = new ArrayList<>();

        reference1 = FirebaseDatabase.getInstance().getReference("Users");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for(String id: usersList) {
                        if(user.getId().equals(id)) {
                            if(mUsers.size() != 0) {
                                for(User user1: mUsers) {
                                    if(!user.getId().equals(user1.getId())) {
                                        mUsers.add(user);
                                    }
                                }
                            }
                            else {
                                mUsers.add(user);
                            }
                        }
                    }
                }

                usersAdapter = new UsersAdapter(getContext(), mUsers);
                recyclerView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
