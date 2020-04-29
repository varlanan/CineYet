package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button; //package for button
import android.view.View;
import android.content.Intent;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    private Button loginButton, signUpButton;
    private static final String TAG="TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.EmailLogin);
        password = (EditText) findViewById(R.id.PasswordLogin);
        loginButton=(Button) findViewById(R.id.LoginButton);
        signUpButton = (Button) findViewById(R.id.SignUpButton);

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.LoginButton:
                openProfilePage();
                break;
            case R.id.SignUpButton:
                openSignUpPage();
                break;
            default:
                break;
        }

//        loginButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                openProfilePage();
//            }
//        });

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

    public void openSignUpPage(){
        Intent myIntent = new Intent(this, SignUpPage.class);
        Log.i(TAG,"openRegisterPage");

        startActivity(myIntent);
    }
}
