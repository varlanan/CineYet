package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.HorizontalScrollView;

public class profile_page extends AppCompatActivity {

    HorizontalScrollView favouriteMovies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //favouriteMovies=(HorizontalScrollView)findViewById(R.id.favmovies);

    }
}
