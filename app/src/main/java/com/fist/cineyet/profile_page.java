package com.fist.cineyet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private MainAdapter mainAdapter;

    FirebaseAuth myFirebaseAuth;
    String new_name, new_interests;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    String currentUserID;
    ArrayList<searchbarItems> favouriteMovies;

    private newsFeedAdapter listAdapter;
    private View myview;

    private CircleImageView profile_img;
    private TextView profile_name, interests;
    private Button bLogOut, editProfile, addFriendButton, messageButton, recommendButton;
    private static final String TAG="Profile Page";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myview= inflater.inflate(R.layout.activity_profile_page, container, false);

        //Firebase Variables
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        //Profile atributes
        profile_name = myview.findViewById(R.id.profile_pic_name);
        interests = myview.findViewById(R.id.profile_interests);
        new_interests = interests.getText().toString();
        bLogOut = myview.findViewById(R.id.LogOutButton);
        editProfile = myview.findViewById(R.id.update_profile);
        addFriendButton = myview.findViewById(R.id.add_friend_button);
        messageButton = myview.findViewById(R.id.message_profile);
        recommendButton = myview.findViewById(R.id.give_rec_profile);
        profile_img = myview.findViewById(R.id.profile_picture_sample);
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


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String image_string=dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(profile_img);

                    new_name=dataSnapshot.child("name").getValue().toString();
                    profile_name.setText(new_name);

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

        //hardcoded newsfeed for now
        ArrayList<newsfeedItems> myMovies=new ArrayList<newsfeedItems>();
        for(int i=0;i<10;i++){
            myMovies.add(new newsfeedItems("Sept-06-2018", new_name, "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic));
        }
        Bundle arguments=getArguments();
        String profileType=arguments.getString("isPersonalProfile");
        modifyButtons(profileType);
        scrollFunction(R.id.sample_favourite_movie,profileType,true);
        scrollFunction(R.id.sample_watch_list,profileType,false);
        listFunction(R.id.activity_scroller,myMovies);
        return myview;
    }

    private void scrollFunction(Integer id, final String profileType, final Boolean isFavouriteList){
        favouriteMoviesLayout=(RecyclerView)  myview.findViewById(id);
        final ArrayList<searchbarItems> moviesList= new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());

        mainAdapter= new MainAdapter(getActivity(),moviesList,true,isFavouriteList,profileType);
        favouriteMoviesLayout.setAdapter(mainAdapter);
        userRef.child(isFavouriteList?"favouritelist":"watchlist").addValueEventListener(new ValueEventListener() {
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
                if(profileType=="PERSONAL")
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
        }
        else{
            //should be already friends
            layout.removeView(addFriendButton);
            layout.removeView(editProfile);
        }
    }
}
