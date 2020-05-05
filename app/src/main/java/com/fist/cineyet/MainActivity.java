package com.fist.cineyet;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    private Button loginButton, signUpButton;
    private static final String TAG="TAG";

    FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        myFirebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.EmailLogin);
        password = (EditText) findViewById(R.id.PasswordLogin);
        loginButton=(Button) findViewById(R.id.LoginButton);
        signUpButton = (Button) findViewById(R.id.SignUpButton);

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        if(myFirebaseAuth.getCurrentUser() != null){
            Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
            openHomePage();
            finish();
        }

//        myAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(myFirebaseUser != null){
//                    Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
//                    openHomePage();
//                    finish();
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Please log in", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.LoginButton:
                String Email = email.getText().toString();
                String Pwd = password.getText().toString();

                if(Email.isEmpty()){
                    email.setError("Please enter an email");
                    email.requestFocus();
                }
                else if(Pwd.isEmpty()){
                    password.setError("Please enter a password");
                    password.requestFocus();
                }
                else if(Email.isEmpty() && Pwd.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(!(Email.isEmpty() && Pwd.isEmpty())) {
                    /* Authenticate the user */
                    myFirebaseAuth.signInWithEmailAndPassword(Email, Pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Log in unsuccessful, please try again.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                /* Upon successfully logging in, user is brought to home page */
                                Toast.makeText(MainActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                                openHomePage();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Something's wrong.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.SignUpButton:
                openSignUpPage();
                break;
            default:
                break;
        }

        Log.i(TAG,"onCreate");

    }
    @Override
    protected void onStart() {
        super.onStart();
//        myFirebaseAuth.addAuthStateListener(myAuthStateListener);
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
    public void openHomePage(){
        Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
        Log.i(TAG,"openRegisterPage");

        startActivity(myIntent);
        //finish();
    }
}
