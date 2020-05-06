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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class profile_page extends Fragment {

    RecyclerView favouriteMoviesLayout;
    ListView activityList;
    MainAdapter mainAdapter;
    TextView name;
    TextView interests;
    Button bLogOut;
    Button editProfile;
    CircleImageView profile_pic;
    DatabaseReference UserRef;
    FirebaseAuth mAuth;
    String currentUserID;
    FirebaseAuth myFirebaseAuth;
    String new_name;
    private FirebaseAuth.AuthStateListener myAuthListener;


    newsFeedAdapter listAdapter;
    private View myview;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile_page);
//        Integer[] moviesArray={R.drawable.groundhogdayposter,R.drawable.movieposter,R.drawable.rearwindowposter,R.drawable.serbianfilmposter,R.drawable.parasiteposter};
//        Integer[] movies2Array={R.drawable.boanposter,R.drawable.littlewomen,R.drawable.midsommarposter,R.drawable.oldboyposter};
//
//        scrollFunction(R.id.sample_favourite_movie,moviesArray);
//        scrollFunction(R.id.sample_watch_list,movies2Array);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myview= inflater.inflate(R.layout.activity_profile_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        name = myview.findViewById(R.id.profile_pic_name);
        interests = myview.findViewById(R.id.profile_interests);
        bLogOut = (Button) myview.findViewById(R.id.LogOutButton);
        editProfile=(Button)myview.findViewById(R.id.update_profile);
        profile_pic=myview.findViewById(R.id.profile_picture_sample);
        new_name=name.getText().toString();
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

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String image_string=dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(profile_pic);


                    new_name=dataSnapshot.child("name").getValue().toString();
                    name.setText(new_name);

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


        Integer[] moviesArray={R.drawable.groundhogdayposter,R.drawable.movieposter,R.drawable.rearwindowposter,R.drawable.serbianfilmposter,R.drawable.parasiteposter};
        Integer[] movies2Array={R.drawable.boanposter,R.drawable.littlewomen,R.drawable.midsommarposter,R.drawable.oldboyposter};
        ArrayList<newsfeedItems> myMovies=new ArrayList<newsfeedItems>();
        for(int i=0;i<10;i++){
            myMovies.add(new newsfeedItems("Sept-06-2018", new_name, "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic));
        }

        Bundle arguments=getArguments();
        String profileType=arguments.getString("isPersonalProfile");
        //change up buttons
        Button addFriendButton=myview.findViewById(R.id.add_friend_button);
        Button messageButton=myview.findViewById(R.id.message_profile);
        Button recommendButton=myview.findViewById(R.id.give_rec_profile);

        ViewGroup layout = (ViewGroup) messageButton.getParent();

        if(profileType=="PERSONAL") {
            if(null!=layout) {//for safety only  as you are doing onClick
                layout.removeView(messageButton);
                layout.removeView(recommendButton);
                layout.removeView(addFriendButton);
            }
        }
        else if(profileType=="NOTFRIENDS"){
            layout.removeView(recommendButton);
        }
        else{
            layout.removeView(addFriendButton);
        }
//        if(arguments.containsKey("updatedName")) {
//
//
//            String newname = arguments.getString("updatedName");
//            if (newname != "")
//                name.setText(newname);
//
//            String newInterests = arguments.getString("updatedInterests");
//            if (newInterests != "")
//                interests.setText(newInterests);
//        }

        scrollFunction(R.id.sample_favourite_movie,moviesArray,profileType);
        scrollFunction(R.id.sample_watch_list,movies2Array,profileType);
        listFunction(R.id.activity_scroller,myMovies);
        return myview;
    }

    private void scrollFunction(Integer id, Integer[] moviesArray,String profileType){
        favouriteMoviesLayout=(RecyclerView)  myview.findViewById(id);
        ArrayList<Integer> moviesList=new ArrayList<Integer>();
        for(int i=0;i<moviesArray.length;i++){
            moviesList.add(moviesArray[i]);
        }
        if(profileType=="PERSONAL")
            moviesList.add(R.drawable.plusbutton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new MainAdapter(getActivity(),moviesList);
        favouriteMoviesLayout.setAdapter(mainAdapter);
    }
    private void listFunction(Integer id,ArrayList<newsfeedItems> myMovies){
        favouriteMoviesLayout=(RecyclerView) myview.findViewById(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());
        listAdapter= new newsFeedAdapter(getActivity(),myMovies);
        favouriteMoviesLayout.setAdapter(listAdapter);

    }
}
