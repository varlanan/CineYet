package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpPage extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button bSignUp;
    FirebaseAuth myFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        myFirebaseAuth = FirebaseAuth.getInstance();
        etUsername = (EditText) findViewById(R.id.UserNameSignUp);
        etEmail = (EditText) findViewById(R.id.EmailSignUp);
        etPassword = (EditText) findViewById(R.id.PasswordSignUp);
        bSignUp = (Button) findViewById(R.id.RegisterButton);

        if(myFirebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        /* Once you sign up, you are redirected back to the login page to sign in */
        bSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String pwd = etPassword.getText().toString();

                if(email.isEmpty()){
                    etEmail.setError("Please enter an email");
                    etEmail.requestFocus();
                }
                else if(pwd.isEmpty()){
                    etPassword.setError("Please enter a password");
                    etEmail.requestFocus();
                }
                else if(pwd.length() < 6){
                    Toast.makeText(SignUpPage.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(SignUpPage.this, "Fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())) {
                    /* User is signed in automatically upon successfully creating a new account */
                    myFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpPage.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpPage.this, "SignUp Unsuccessful, please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                /* Upon successfully registering, user redirected to login page */
                                Toast.makeText(SignUpPage.this, "User created", Toast.LENGTH_SHORT).show();
                                Intent SignUpIntent = new Intent(SignUpPage.this, HomeActivity.class);
                                startActivity(SignUpIntent);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignUpPage.this, "Something's wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
