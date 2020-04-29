package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button; //package for button
import android.view.View;
import android.content.Intent;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private static final String TAG="TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView facebook = findViewById(R.id.FacebookLogo);
        facebook.bringToFront();

        ImageView google = findViewById(R.id.GoogleLogo);
        google.bringToFront();
        loginButton=(Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openProfilePage();
            }
        });

        Log.i(TAG,"onCreate");

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    public void openProfilePage(){
        Intent myintent=new Intent(this, profile_page.class);
        Log.i(TAG,"openProfilePage");

        startActivity(myintent);

    }
}
