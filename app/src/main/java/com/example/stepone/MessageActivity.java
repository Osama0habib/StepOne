package com.example.stepone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stepone.Adapter.MessageAdapter;
import com.example.stepone.Model.Chat;
import com.example.stepone.Model.User;
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
CircleImageView profile_image;
TextView username;
FirebaseUser fUser;
DatabaseReference reference;
Intent intent;
EditText text_send;
ImageButton btn_send;
MessageAdapter messageAdapter;
List<Chat> mChat;
RecyclerView recyclerView;
ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(MessageActivity.this , MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));            }
        });


        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
       // recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        text_send=findViewById(R.id.text_send);
        btn_send =findViewById(R.id.btn_send);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fUser.getUid(),userId,msg);
                }
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURl()).into(profile_image);
                }
                readMessages(fUser.getUid() , userId , user.getImageURl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userId);
    }

    private void seenMessage(String userId){
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String sender , String receiver , String message ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender" , sender);
        hashMap.put("receiver" , receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);
    }
    private void readMessages(String myId , String userId , String imageURL){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren() ){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myId)&& chat.getSender().equals(userId) ||chat.getReceiver().equals(userId)&& chat.getSender().equals(myId)){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this , mChat , imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("status" , status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("Offline");
    }
}