package com.fist.cineyet;

import android.renderscript.Sampler;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> userMessageList;
    FirebaseAuth mAuth;
    DatabaseReference usersDatabaseRef;
    public MessageAdapter(List<Messages> userMessageList){
        this.userMessageList=userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_layout,parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderID=mAuth.getCurrentUser().getUid();
        Messages msgs=userMessageList.get(position);
        String fromUserID=msgs.getFrom();
        String fromMessageType=msgs.getType();
        Log.d("My tag",fromMessageType+fromUserID);
        usersDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image=dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(fromMessageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){
                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setText(msgs.getMessage());
                holder.SenderMessageText.setGravity(Gravity.LEFT);
            }
            else{
                holder.SenderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setText(msgs.getMessage());
                holder.receiverMessageText.setGravity(Gravity.LEFT);

            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView SenderMessageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            SenderMessageText=itemView.findViewById(R.id.message_sender_text);
            receiverMessageText=itemView.findViewById(R.id.message_receiver_text);
            receiverProfileImage=itemView.findViewById(R.id.receiver_profile_pic);
        }
    }
}
