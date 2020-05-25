package com.fist.cineyet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HotFragment extends Fragment {

    View myview;
    RecyclerView newsfeedLayout;
    newsFeedAdapter mainAdapter;

    private FirebaseAuth myFirebaseAuth;
    private DatabaseReference movieRef;
    private DatabaseReference userRef;
    String currentUserID;


    ArrayList<newsfeedItems> movieReviews = new ArrayList<newsfeedItems>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        movieRef = FirebaseDatabase.getInstance().getReference().child("ReviewedMovies");
        myview = inflater.inflate(R.layout.fragment_hot, container, false);

        /* Retrieve all reviews for the movie with the given imdbID */
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(imdbID)){
                for(DataSnapshot snapshot_movie_ids : dataSnapshot.getChildren()){
                    /* all the reviews under one movie ID */
                    for(DataSnapshot snapshot_children_of_movie : snapshot_movie_ids.getChildren()) {
                        newsfeedItems newNewsfeedItem =
                                new newsfeedItems(snapshot_children_of_movie.child("date").getValue().toString(),
                                snapshot_children_of_movie.child("userName").getValue().toString(),
                                snapshot_children_of_movie.child("movieTitle").getValue().toString(),
                                snapshot_children_of_movie.child("review").getValue().toString(),
                                "Reviewed: ", snapshot_children_of_movie.child("moviePoster").getValue().toString(),
                                snapshot_children_of_movie.child("userProfilePic").getValue().toString(),
                                snapshot_children_of_movie.child("rating").getValue().toString(), snapshot_children_of_movie.getKey(),
                                        snapshot_children_of_movie.child("userID").getValue().toString());
                        movieReviews.add(newNewsfeedItem);

                        scrollFunction(R.id.newsFeed, movieReviews);
                    }
                }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        ArrayList<newsfeedItems> myMovies = new ArrayList<newsfeedItems>();
//        for(int i=0;i<10;i++){
//            myMovies.add(new newsfeedItems("Sept-06-2018", "YiFei Tang", "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
//                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic, "4.5"));
//        }
//        scrollFunction(R.id.newsFeed,myMovies);
        return myview;
    }

    private void scrollFunction(Integer id, ArrayList<newsfeedItems> moviesList){
        newsfeedLayout=(RecyclerView) myview.findViewById(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        newsfeedLayout.setLayoutManager(layoutManager);
        newsfeedLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new newsFeedAdapter(getActivity(),moviesList);
        newsfeedLayout.setAdapter(mainAdapter);
    }
}

