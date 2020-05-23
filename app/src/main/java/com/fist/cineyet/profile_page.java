package com.fist.cineyet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class profile_page extends Fragment {

    private RecyclerView favouriteMoviesLayout;
    private ListView activityList;

    FirebaseAuth myFirebaseAuth;
    String new_name, new_interests;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    private DatabaseReference friendsRequestRef;
    String currentUserID, profile_id;
    ArrayList<searchbarItems> favouriteMovies;

    private newsFeedAdapter listAdapter;
    private View myview;
    boolean friendRequestSent;
    private CircleImageView profile_img;
    private TextView interests;
    private EditText profile_name;
    private Button bLogOut, editProfile, addFriendButton, messageButton, recommendButton, friendsListButton;
    private static final String TAG="Profile Page";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myview= inflater.inflate(R.layout.activity_profile_page, container, false);
        final Bundle arguments=getArguments();
        final String profileType=arguments.getString("isPersonalProfile");
        //Firebase Variables
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        if(profileType.equals("PERSONAL"))
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        else{
            profile_id=arguments.getString("UserID");
            userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(profile_id);
        }
        friendsRequestRef=FirebaseDatabase.getInstance().getReference().child("friend_request");

        //Profile atributes
        friendRequestSent=false;
        profile_name = myview.findViewById(R.id.profile_pic_name);
        interests = myview.findViewById(R.id.profile_interests);
        new_interests = interests.getText().toString();
        bLogOut = myview.findViewById(R.id.LogOutButton);
        editProfile = myview.findViewById(R.id.update_profile);
        addFriendButton = myview.findViewById(R.id.add_friend_button);
        messageButton = myview.findViewById(R.id.message_profile);
        recommendButton = myview.findViewById(R.id.give_rec_profile);
        profile_img = myview.findViewById(R.id.profile_picture_sample);
        friendsListButton=myview.findViewById(R.id.profile_friends_list_button);
        new_name = profile_name.getText().toString();

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent meinTent = new Intent((Context)getActivity(), MainActivity.class);
                startActivity(meinTent);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent((Context)getActivity(), UpdateProfileActivity.class);
                startActivity(myIntent);
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addFriendButton.setEnabled(false);
                if(friendRequestSent==false)
                    sendFriendRequest();
                else
                    cancelFriendRequest();
            }


        });
        friendsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(getActivity(),FriendsListActivity.class);
                if(profileType.equals("PERSONAL"))
                    myIntent.putExtra("UserID", currentUserID);
                else
                    myIntent.putExtra("UserID",profile_id);
                myIntent.putExtra("isPersonalProfile",profileType);
                startActivity(myIntent);

            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("profileimage").getValue()!=null){
                        String image_string=dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(profile_img);

                    }
                    if(dataSnapshot.child("name").getValue()!=null){
                        new_name = dataSnapshot.child("name").getValue().toString();
                        String[] name_split = new_name.split(" ");
                        String uppercase_name = name_split[0].substring(0, 1).toUpperCase() + name_split[0].substring(1).toLowerCase() + " "
                                +  name_split[1].substring(0, 1).toUpperCase() + name_split[1].substring(1).toLowerCase();
                        profile_name.setText(uppercase_name);
                    }

                    if(dataSnapshot.child("interests").getValue()!=null) {
                        String new_interests = dataSnapshot.child("interests").getValue().toString();
                        interests.setText(new_interests);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(getActivity(), AddToListActivity.class);
                if(arguments.containsKey("fromHome"))
                    myIntent.putExtra("fromHome",true);
                myIntent.putExtra("listType","friendsreclist"); //type of list
                myIntent.putExtra("addButton",true); //add button to add movie to their list
                myIntent.putExtra("profile_id",profile_id); //
                getActivity().startActivity(myIntent);
            }
        });

        //hardcoded newsfeed for now
        ArrayList<newsfeedItems> myMovies=new ArrayList<newsfeedItems>();
        for(int i=0;i<10;i++){
            myMovies.add(new newsfeedItems("Sept-06-2018", new_name, "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic));
        }

        modifyButtons(profileType);
        scrollFunction(R.id.sample_favourite_movie,profileType,"favouritelist");
        scrollFunction(R.id.sample_watch_list,profileType,"watchlist");
        listFunction(R.id.activity_scroller,myMovies);
        return myview;
    }

    private void cancelFriendRequest() {
        friendsRequestRef.child(currentUserID).child(profile_id).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendsRequestRef.child(profile_id).child(currentUserID).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(Void aVoid) {
                        addFriendButton.setEnabled(true);
                        Resources res = getResources();
                        Drawable drawable = res.getDrawable(R.drawable.add_friend_icon);
                        addFriendButton.setBackground(drawable);
                        friendRequestSent=false;
                    }
                });
            }
        });
    }

    private void sendFriendRequest() {
        friendsRequestRef.child(currentUserID).child(profile_id).child("request_type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendsRequestRef.child(profile_id).child(currentUserID).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(Void aVoid) {
                        addFriendButton.setEnabled(true);
                        Resources res = getResources();
                        Drawable drawable = res.getDrawable(R.drawable.ic_cancel_black_24dp);
                        addFriendButton.setBackground(drawable);
                        friendRequestSent=true;
                    }
                });
            }
        });

    }

    private void scrollFunction(Integer id, final String profileType, final String listType){
        favouriteMoviesLayout=(RecyclerView)  myview.findViewById(id);
        final ArrayList<searchbarItems> moviesList= new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());

       final MainAdapter mainAdapter= new MainAdapter(getActivity(),moviesList,profileType.equals("PERSONAL"),listType,profileType);
        favouriteMoviesLayout.setAdapter(mainAdapter);
        userRef.child(listType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String myid=postSnapshot.getKey();
                    String myyear=postSnapshot.child("year").getValue().toString();
                    String mytitle=postSnapshot.child("title").getValue().toString();
                    String myurl=postSnapshot.child("poster").getValue().toString();
                    Log.d(TAG,mytitle+myurl+myyear+myid);

                    moviesList.add(new searchbarItems(mytitle,myyear,myurl,myid));
                }
                if(profileType.equals("PERSONAL"))
                    moviesList.add(new searchbarItems("","","",""));
                mainAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    private void listFunction(Integer id,ArrayList<newsfeedItems> myMovies){
        favouriteMoviesLayout=(RecyclerView) myview.findViewById(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());
        listAdapter= new newsFeedAdapter(getActivity(),myMovies);
        favouriteMoviesLayout.setAdapter(listAdapter);

    }
    private void modifyButtons(String profileType){
        ViewGroup layout = (ViewGroup) messageButton.getParent();

        if(profileType.equals("PERSONAL")) {
            if(null!=layout) {//for safety only  as you are doing onClick
                layout.removeView(messageButton);
                layout.removeView(recommendButton);
                layout.removeView(addFriendButton);
            }
        }

        else if(profileType.equals("NOTFRIENDS")){
            layout.removeView(recommendButton);
            layout.removeView(editProfile);
            layout.removeView(bLogOut);
            friendsRequestRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(profile_id)){
                        String request_type=dataSnapshot.child(profile_id).child("request_type").getValue().toString();
                        if(request_type.equals("sent")){
                            addFriendButton.setEnabled(true);
                            Resources res = getResources();
                            Drawable drawable = res.getDrawable(R.drawable.ic_cancel_black_24dp);
                            addFriendButton.setBackground(drawable);

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            //should be already friends
            layout.removeView(addFriendButton);
            layout.removeView(editProfile);
            layout.removeView(bLogOut);
        }
    }
}
