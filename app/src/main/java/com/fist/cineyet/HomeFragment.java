package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class HomeFragment extends Fragment {

    RecyclerView favouriteMoviesLayout;
    MainAdapter mainAdapter;
    FirebaseAuth myFirebaseAuth;
    String currentUserID;
    Button searchPeople;
    Button searchMovies;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    View myview;
    final String TAG="home fragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview= inflater.inflate(R.layout.fragment_home, container, false);
        searchPeople=myview.findViewById(R.id.search_people_button);
        searchMovies=myview.findViewById(R.id.search_movie_button);
        searchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(getActivity(),FindPeopleActivity.class);
                startActivity(myIntent);
            }
        });
        searchMovies.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(getActivity(),AddToListActivity.class);
                myIntent.putExtra("isFavourite",false);

                myIntent.putExtra("addButton",false);
                startActivity(myIntent);
            }
        });
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        scrollFunction(R.id.friends_rec,"friendsreclist");
        //scrollFunction(R.id.mood_rec,"moodlist");
        return myview;
    }

    private void scrollFunction(Integer id, final String listType){
        Log.d(TAG,"scroll on home  called");
        favouriteMoviesLayout=(RecyclerView)  myview.findViewById(id);
        final ArrayList<searchbarItems> moviesList= new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());

        mainAdapter= new MainAdapter(getActivity(),moviesList,false,listType,"PERSONAL");
        favouriteMoviesLayout.setAdapter(mainAdapter);
        userRef.child(listType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    String myid=postSnapshot.getKey();
                    String myyear=postSnapshot.child("year").getValue().toString();
                    String mytitle=postSnapshot.child("title").getValue().toString();
                    String myurl=postSnapshot.child("poster").getValue().toString();
                    Log.d(TAG,"on data change"+mytitle+myyear+myurl+myid);

                    moviesList.add(new searchbarItems(mytitle,myyear,myurl,myid));

                }
                Log.d(TAG, "dataset changed");
                mainAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
