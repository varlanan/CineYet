package com.fist.cineyet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    Button bLogin, bSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.EmailLogin);
        password = (EditText) findViewById(R.id.PasswordLogin);
        bLogin = (Button) findViewById(R.id.LoginButton);
        bSignUp = (Button) findViewById(R.id.SignUpButton);
        bLogin.setOnClickListener(this);
        bSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.LoginButton:
                break;
            case R.id.SignUpButton:
                startActivity(new Intent(this, RegisterScreen.class));
                break;
            default:
                break;
        }
    }
}
