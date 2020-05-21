package com.fist.cineyet;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestFragment extends androidx.fragment.app.Fragment {
    View myview;
    public final String TAG="friend request fragment";

    private DatabaseReference userRef;
    private DatabaseReference requestRef;
    private DatabaseReference friendRef;
    RecyclerView searchPeopleRecycler;
    String currentUserID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef= FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FirebaseAuth myFirebaseAuth=FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        myview= inflater.inflate(R.layout.fragment_friend_list, container, false);
        searchPeopleRecycler=myview.findViewById(R.id.friends_list_recycler);
        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayAllRequests();
    }

    private void displayAllRequests(){
        Query myquery=requestRef.child(currentUserID).orderByChild("request_type").equalTo("received"); //all the accounts that sent the current user requests
        FirebaseRecyclerOptions<FindRequests> options=new FirebaseRecyclerOptions.Builder<FindRequests>()
                .setQuery(myquery,FindRequests.class)
                .build();

        FirebaseRecyclerAdapter<FindRequests, FriendRequestFragment.FindFriendsViewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindRequests, FriendRequestFragment.FindFriendsViewholder>(options){

            @NonNull
            @Override
            public FriendRequestFragment.FindFriendsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_request_list,parent,false);
                FriendRequestFragment.FindFriendsViewholder viewholder=new FriendRequestFragment.FindFriendsViewholder(view);
                return viewholder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendRequestFragment.FindFriendsViewholder holder, final int position, @NonNull final FindRequests model) {
                holder.setRequest_type(model.getRequest_type());
                final String requesterID=getRef(position).getKey();
                Log.d(TAG,"bind onposition "+position);

                userRef.child(requesterID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.child("name").getValue()!=null){
                                final String requesterName=dataSnapshot.child("name").getValue().toString();
                                String[] name_split = requesterName.split(" ");
                                String uppercase_name = name_split[0].substring(0, 1).toUpperCase() + name_split[0].substring(1).toLowerCase() + " "
                                        +  name_split[1].substring(0, 1).toUpperCase() + name_split[1].substring(1).toLowerCase();
                                holder.setName(uppercase_name);
                            }
                            if(dataSnapshot.child("interests").getValue()!=null){
                                final String requesterInterests=dataSnapshot.child("interests").getValue().toString();
                                holder.setInterests(requesterInterests);

                            }
                            if(dataSnapshot.child("profileimage").getValue()!=null){
                                final String requesterPicture=dataSnapshot.child("profileimage").getValue().toString();
                                holder.setProfileimage(requesterPicture);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment profFrag=new profile_page();
                        Bundle mybund=new Bundle();
                        String userKey=getRef(position).getKey();
                        if(userKey.equals(currentUserID))
                            mybund.putString("isPersonalProfile","PERSONAL");
                        else{
                            mybund.putString("isPersonalProfile","NOTFRIENDS"); //change later when you figure out friends lists
                            mybund.putString("UserID",getRef(position).getKey());
                        }
                        profFrag.setArguments(mybund);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends ,profFrag).commit();

                    }
                });
                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AcceptFriendRequest(requesterID);
                       // getRef(position).remove();
                    }
                });
                holder.declineButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        removeFriendRequest(requesterID);
                        //getRef(position).remove();

                    }
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        searchPeopleRecycler.setLayoutManager(layoutManager);
        firebaseRecyclerAdapter.startListening();

        searchPeopleRecycler.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FindFriendsViewholder extends RecyclerView.ViewHolder{
        TextView myname;
        CircleImageView image;
        TextView myinterests;
        ConstraintLayout parent;
        Button acceptButton;
        Button declineButton;
        String request_type;

        public FindFriendsViewholder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.friend_request_list_parent);
            acceptButton = itemView.findViewById(R.id.accept_friend_request_button);
            declineButton = itemView.findViewById(R.id.delete_friend_request_button);

        }
        public void setProfileimage(String profileimage) {
            image = itemView.findViewById(R.id.friend_request_picture);
            Picasso.get().load(profileimage)
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .into(image);
        }

        public void setName(String name) {
            myname = itemView.findViewById(R.id.friend_request_name);

            this.myname.setText(name);


        }
        public void setInterests(String interests) {
            myinterests = itemView.findViewById(R.id.friend_request_interests);

            this.myinterests.setText(interests);
        }
        public void setRequest_type(String request_type) {
            this.request_type = request_type;
        }
    }
    private void AcceptFriendRequest(final String sender_id) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat current_date = new SimpleDateFormat("dd-MM-YYYY");
        final String saveCurrentDate = current_date.format(date.getTime());
        friendRef.child(currentUserID).child(sender_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRef.child(sender_id).child(currentUserID).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeFriendRequest(sender_id);
                    }
                });
            }
        });
    }
    private void removeFriendRequest(final String sender_id){
        requestRef.child(currentUserID).child(sender_id).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                requestRef.child(sender_id).child(currentUserID).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }
}
