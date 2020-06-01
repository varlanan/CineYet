package com.fist.cineyet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
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
    private DatabaseReference movieRef, userRef, friendRef;
    String currentUserID;

    private Chip chipAll, chipMyReviews, chipFriendReviews;
    ArrayList<String> selectedFilters;
    ArrayList<String> friendList;
    Boolean checkedChange;

    ArrayList<newsfeedItems> movieReviews = new ArrayList<newsfeedItems>();;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        movieRef = FirebaseDatabase.getInstance().getReference().child("ReviewedMovies");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        myview = inflater.inflate(R.layout.fragment_hot, container, false);

        chipAll = myview.findViewById(R.id.all_filter_chip);
        chipMyReviews = myview.findViewById(R.id.my_reviews_filter_chip);
        chipFriendReviews = myview.findViewById(R.id.friends_reviews_filter_chip);

        /* Get current user's friends */
        friendList = new ArrayList<>();
        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUserID)){
                    for(DataSnapshot friend : dataSnapshot.child(currentUserID).getChildren()){
                        friendList.add(friend.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Display reviews based on filters */
        checkedChange = false;
        movieReviews.clear();
        selectedFilters = new ArrayList<>();
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedChange = true;
                if(isChecked){
                    movieReviews.clear();
                    selectedFilters.clear();
                    selectedFilters.add(buttonView.getText().toString());
                    getNewsFeedList();
                }
                else{
                    movieReviews.clear();
                    selectedFilters.remove(buttonView.getText().toString());
                    getNewsFeedList();
                }

            }
        };

        if(!checkedChange){
            movieReviews.clear();
            getNewsFeedList();
        }
        else {
            checkedChange = false;
        }

        chipAll.setOnCheckedChangeListener(checkedChangeListener);
        chipMyReviews.setOnCheckedChangeListener(checkedChangeListener);
        chipFriendReviews.setOnCheckedChangeListener(checkedChangeListener);

        return myview;
    }

    private void scrollFunction(Integer id, ArrayList<newsfeedItems> moviesList){
        newsfeedLayout=(RecyclerView) myview.findViewById(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        newsfeedLayout.setLayoutManager(layoutManager);
        newsfeedLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new newsFeedAdapter(getActivity(),moviesList);
        newsfeedLayout.setAdapter(mainAdapter);
        if(moviesList == null){
            Toast.makeText(getContext(), "No reviews available under this category", Toast.LENGTH_SHORT).show();
        }
    }

    private void getNewsFeedList() {
        movieReviews.clear();
        /* Retrieve all reviews for the movie with the given imdbID */
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot_movie_ids : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot_children_of_movie : snapshot_movie_ids.getChildren()) {
                        if(selectedFilters.size() == 0){
                            createNewsFeedItem(snapshot_children_of_movie);
                        }
                        else {
                            switch (selectedFilters.get(0)) {
                                case "My Reviews":
                                    DataSnapshot rootChild = snapshot_children_of_movie;
                                    String userIDString = snapshot_children_of_movie.child("userID").getValue().toString();
                                    if( userIDString.equals(currentUserID) ) {
                                        createNewsFeedItem(rootChild);
                                    }
                                    break;
                                case "Friends Reviews":
                                    if( friendList.contains( snapshot_children_of_movie.child("userID").getValue().toString() ) ){
                                        createNewsFeedItem(snapshot_children_of_movie);
                                    }
                                    break;
                                case "All":
                                default:
                                    createNewsFeedItem(snapshot_children_of_movie);
                                    break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewsFeedItem(DataSnapshot snapshot){
        newsfeedItems newNewsfeedItem = null;

        if(snapshot.hasChild("movieTitle")) {
            newNewsfeedItem =
                    new newsfeedItems(snapshot.child("date").getValue().toString(),
                            snapshot.child("userName").getValue().toString(),
                            snapshot.child("movieTitle").getValue().toString(),
                            snapshot.child("review").getValue().toString(),
                            "Reviewed: ", snapshot.child("moviePoster").getValue().toString(),
                            snapshot.child("userProfilePic").getValue().toString(),
                            snapshot.child("rating").getValue().toString(),
                            snapshot.getKey(), snapshot.child("userID").getValue().toString());
        }
        else {
            newNewsfeedItem =
                    new newsfeedItems(snapshot.child("date").getValue().toString(),
                            snapshot.child("userName").getValue().toString(),
                            "DON'T BE HASTY",
                            snapshot.child("review").getValue().toString(),
                            "Reviewed: ", "https://www.101soundboards.com/storage/board_pictures/27046.jpg?c=1583177855",
                            snapshot.child("userProfilePic").getValue().toString(),
                            snapshot.child("rating").getValue().toString(),
                            snapshot.getKey(), snapshot.child("userID").getValue().toString());
        }
        if(newNewsfeedItem != null){
            movieReviews.add(newNewsfeedItem);
            scrollFunction(R.id.newsFeed, movieReviews);
        }

    }

}

