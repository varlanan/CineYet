package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView facebook = findViewById(R.id.FacebookLogo);
        facebook.bringToFront();

        ImageView google = findViewById(R.id.GoogleLogo);
        google.bringToFront();
    }
}
