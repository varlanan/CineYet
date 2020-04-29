package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EditText emailLogin = (EditText) findViewById(R.id.EmailLogin);
//        ImageView facebook = findViewById(R.id.FacebookLogo);
//        facebook.bringToFront();
//
//        ImageView google = findViewById(R.id.GoogleLogo);
//        google.bringToFront();
    }
}
