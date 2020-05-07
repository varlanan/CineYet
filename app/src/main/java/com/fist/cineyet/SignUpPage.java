package com.fist.cineyet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpPage extends AppCompatActivity {

    private EditText etName, etUsername, etEmail, etPassword, etConfirmPassword;
    private Button bSignUp;
    private FirebaseAuth myFirebaseAuth;
    private DatabaseReference userRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        myFirebaseAuth = FirebaseAuth.getInstance();

        etName = (EditText) findViewById(R.id.NameSignUp);
        etUsername = (EditText) findViewById(R.id.UserNameSignUp);
        etEmail = (EditText) findViewById(R.id.EmailSignUp);
        etPassword = (EditText) findViewById(R.id.passwordSignUp);
        etConfirmPassword = (EditText) findViewById(R.id.ConfirmPasswordSignUp);
        bSignUp = (Button) findViewById(R.id.RegisterButton);

        if(myFirebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        /* Once you sign up, you are redirected back to the login page to sign in */
        bSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String pwd = etPassword.getText().toString();
                String confPwd = etConfirmPassword.getText().toString();

                if(username.isEmpty()){
                    etUsername.setError("Please enter your username");
                    etUsername.requestFocus();
                }
                else if(name.isEmpty()){
                    etName.setError("Please enter your name");
                    etName.requestFocus();
                }
                else if(email.isEmpty()){
                    etEmail.setError("Please enter your email address");
                    etEmail.requestFocus();
                }
                else if(pwd.isEmpty()){
                    etPassword.setError("Please enter a password");
                    etPassword.requestFocus();
                }
                else if(confPwd.isEmpty()){
                    etConfirmPassword.setError("Please confirm your password");
                    etConfirmPassword.requestFocus();
                }
                else if(pwd.length() < 6){
                    Toast.makeText(SignUpPage.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!pwd.equals(confPwd)){
                    Toast.makeText(SignUpPage.this, "Passwords do no match", Toast.LENGTH_SHORT).show();
                }
//                else if(email.isEmpty() && pwd.isEmpty()){
//                    Toast.makeText(SignUpPage.this, "Two or more fields are empty", Toast.LENGTH_SHORT).show();
//                }
                else {

                    /* User is signed in automatically upon successfully creating a new account */
                    myFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpPage.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpPage.this, "Sign Up unsuccessful, please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                /* Upon successfully registering, user redirected to login page */
                                currentUserID = myFirebaseAuth.getCurrentUser().getUid();
                                userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                                /* Save user information */
                                HashMap userMap = new HashMap();
                                userMap.put("username", username);
                                userMap.put("name", name);
                                userMap.put("email", email);
                                userMap.put("password", pwd);
                                userMap.put("interests", "");
                                userMap.put("profileimage","https://firebasestorage.googleapis.com/v0/b/cineyet-4fa96.appspot.com/o/Profile%20Images%2FROoU5UMYpuOCQTMPAIg0gTuBdVV2.jpg?alt=media&token=745ddfd7-42a8-4a9e-aa82-f390d4800dbf");
                                /* Might be a this point where we store a generic profile pic and have empty lists of movie made to be updated later */
                                userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignUpPage.this, "User account created", Toast.LENGTH_SHORT).show();
                                            Intent SignUpIntent = new Intent(SignUpPage.this, HomeActivity.class);
                                            startActivity(SignUpIntent);
                                        }
                                        else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(SignUpPage.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
//                else {
//                    Toast.makeText(SignUpPage.this, "Something's wrong.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = myFirebaseAuth.getCurrentUser();

        if(currentUser != null){
            openHomePage();
        }
    }

    public void openHomePage(){
        Intent myIntent = new Intent(SignUpPage.this, HomeActivity.class);
        startActivity(myIntent);
        finish();
    }
}
