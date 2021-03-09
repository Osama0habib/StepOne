package com.example.stepone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stepone.MessageActivity;
import com.example.stepone.Model.Chat;
import com.example.stepone.Model.User;
import com.example.stepone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String theLastMessage;


    public UserAdapter (Context mContext , List<User> mUsers , boolean isChat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent , false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURl().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImageURl()).into(holder.profile_image);
        }
        if(isChat){
            lastMessage(user.getId(),holder.last_msg);
        }else {
            holder.last_msg.setVisibility(View.GONE);
        }
        if(isChat){
            if(user.getStatus().equals("Online")){
                holder.image_online.setVisibility(View.VISIBLE);
                holder.image_offline.setVisibility(View.GONE);
            }else {
                holder.image_offline.setVisibility(View.VISIBLE);
                holder.image_online.setVisibility(View.GONE);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MessageActivity.class);
                intent.putExtra("userId",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public ImageView image_online;
        public ImageView image_offline;
        public TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            image_online = itemView.findViewById(R.id.image_online);
            image_offline = itemView.findViewById(R.id.image_offline);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
    private void lastMessage(String userId , TextView last_msg){
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid()) ){
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage ="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
