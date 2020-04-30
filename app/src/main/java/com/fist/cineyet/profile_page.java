package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;

public class profile_page extends AppCompatActivity {

    RecyclerView favouriteMoviesLayout;
    MainAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Integer[] moviesArray={R.drawable.groundhogdayposter,R.drawable.movieposter,R.drawable.rearwindowposter,R.drawable.serbianfilmposter,R.drawable.parasiteposter};
        Integer[] movies2Array={R.drawable.boanposter,R.drawable.littlewomen,R.drawable.midsommarposter,R.drawable.oldboyposter};

        scrollFunction(R.id.sample_favourite_movie,moviesArray);
        scrollFunction(R.id.sample_watch_list,movies2Array);
    }
    private void scrollFunction(Integer id, Integer[] moviesArray){
        favouriteMoviesLayout=(RecyclerView)findViewById(id);
        ArrayList<Integer> moviesList=new ArrayList<Integer>();
        for(int i=0;i<moviesArray.length;i++){
            moviesList.add(moviesArray[i]);
        }
        moviesList.add(R.drawable.plusbutton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(profile_page.this,LinearLayoutManager.HORIZONTAL,false);
        favouriteMoviesLayout.setLayoutManager(layoutManager);
        favouriteMoviesLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new MainAdapter(profile_page.this,moviesList);
        favouriteMoviesLayout.setAdapter(mainAdapter);
    }
}
