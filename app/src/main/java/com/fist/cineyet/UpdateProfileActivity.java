package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText name;
    EditText username;
    EditText interests;
    EditText email;
    ImageView profile_pic;
    Button submit;
    Button update_profile_pic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name=(EditText) findViewById(R.id.edit_name);
        username=(EditText) findViewById(R.id.edit_username);
         interests=(EditText) findViewById(R.id.edit_interests);
         email=(EditText)findViewById(R.id.edit_email);
         profile_pic=findViewById(R.id.edit_profile_pic);
        update_profile_pic=findViewById(R.id.edit_profile_pic_button);
        submit=findViewById(R.id.edit_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_str=name.getText().toString();
                String username_str=username.getText().toString();
                String interests_str=interests.getText().toString();
                String email_str=interests.getText().toString();
                Intent intent = new Intent(UpdateProfileActivity.this,HomeActivity.class);

                intent.putExtra("name",name_str);
                intent.putExtra("username",username_str);
                intent.putExtra("interests",interests_str);
                intent.putExtra("email",email_str);
                startActivity(intent);
            }
        });


    }

}
