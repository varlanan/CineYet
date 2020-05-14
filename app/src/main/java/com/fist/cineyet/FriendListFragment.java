package com.fist.cineyet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListFragment extends Fragment {
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
        Query myquery=friendRef.child(currentUserID); //all the accounts that sent the current user requests
        FirebaseRecyclerOptions<FindRequests> options=new FirebaseRecyclerOptions.Builder<FindRequests>()
                .setQuery(myquery,FindRequests.class)
                .build();

        FirebaseRecyclerAdapter<FindRequests, FindFriendsHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindRequests, FindFriendsHolder>(options){

            @NonNull
            @Override
            public FindFriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);
                FindFriendsHolder viewholder=new FindFriendsHolder(view);
                return viewholder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsHolder holder, final int position, @NonNull final FindRequests model) {
                final String friendID=getRef(position).getKey();
                Log.d(TAG,"bind onposition "+position);

                userRef.child(friendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final String friendName=dataSnapshot.child("name").getValue().toString();
                            final String friendInterests=dataSnapshot.child("interests").getValue().toString();
                            final String friendPicture=dataSnapshot.child("profileimage").getValue().toString();
                            holder.setName(friendName);
                            holder.setInterests(friendInterests);
                            holder.setProfileimage(friendPicture);
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
                            mybund.putString("isPersonalProfile","FRIENDS"); //change later when you figure out friends lists
                            mybund.putString("UserID",getRef(position).getKey());
                        }
                        profFrag.setArguments(mybund);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends ,profFrag).commit();

                    }
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        searchPeopleRecycler.setLayoutManager(layoutManager);
        firebaseRecyclerAdapter.startListening();

        searchPeopleRecycler.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FindFriendsHolder extends RecyclerView.ViewHolder{
        TextView myname;
        CircleImageView image;
        TextView myinterests;
        ConstraintLayout parent;

        public FindFriendsHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.friend_list_parent);

        }
        public void setProfileimage(String profileimage) {
            image=itemView.findViewById(R.id.friend_picture);
            Picasso.get().load(profileimage)
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .into(image);
        }

        public void setName(String name) {
            myname=itemView.findViewById(R.id.friend_name);

            this.myname.setText(name);


        }
        public void setInterests(String interests) {
            myinterests=itemView.findViewById(R.id.friend_interests);

            this.myinterests.setText(interests);
        }
    }
}
