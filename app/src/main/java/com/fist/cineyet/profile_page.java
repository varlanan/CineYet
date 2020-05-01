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

import java.util.ArrayList;

public class profile_page extends Fragment {

    RecyclerView favouriteMoviesLayout;
    MainAdapter mainAdapter;
    View myview;
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
        Integer[] moviesArray={R.drawable.groundhogdayposter,R.drawable.movieposter,R.drawable.rearwindowposter,R.drawable.serbianfilmposter,R.drawable.parasiteposter};
        Integer[] movies2Array={R.drawable.boanposter,R.drawable.littlewomen,R.drawable.midsommarposter,R.drawable.oldboyposter};

        scrollFunction(R.id.sample_favourite_movie,moviesArray);
        scrollFunction(R.id.sample_watch_list,movies2Array);
        return myview;
    }

    private void scrollFunction(Integer id, Integer[] moviesArray){
        favouriteMoviesLayout=(RecyclerView)  myview.findViewById(id);
        ArrayList<Integer> moviesList=new ArrayList<Integer>();
        for(int i=0;i<moviesArray.length;i++){
            moviesList.add(moviesArray[i]);
        }
        moviesList.add(R.drawable.plusbutton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new MainAdapter(getActivity(),moviesList);
        favouriteMoviesLayout.setAdapter(mainAdapter);
    }
}
