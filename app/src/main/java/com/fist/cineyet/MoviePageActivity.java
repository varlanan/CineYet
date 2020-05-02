package com.fist.cineyet;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MoviePageActivity extends AppCompatActivity {
    private static final String TAG="MainAdapter";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);
        Log.d(TAG,"oncreate started");
    }
}

