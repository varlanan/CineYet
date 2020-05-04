package com.fist.cineyet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class profile_page extends Fragment {

    RecyclerView favouriteMoviesLayout;
    ListView activityList;
    MainAdapter mainAdapter;
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

        //hardcoded for now
        myview= inflater.inflate(R.layout.activity_profile_page, container, false);
        Integer[] moviesArray={R.drawable.groundhogdayposter,R.drawable.movieposter,R.drawable.rearwindowposter,R.drawable.serbianfilmposter,R.drawable.parasiteposter};
        Integer[] movies2Array={R.drawable.boanposter,R.drawable.littlewomen,R.drawable.midsommarposter,R.drawable.oldboyposter};
        ArrayList<newsfeedItems> myMovies=new ArrayList<newsfeedItems>();
        for(int i=0;i<10;i++){
            myMovies.add(new newsfeedItems("Sept-06-2018", "YiFei Tang", "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic));
        }


        String profileType=getArguments().getString("isPersonalProfile");
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
