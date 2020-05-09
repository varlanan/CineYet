package com.fist.cineyet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpdateProfileActivity extends AppCompatActivity {
    EditText name, username, interests, email;
    CircleImageView profile_pic;
    Button submit, update_profile_pic;

    FirebaseAuth mAuth;
    String currentUserID, newName, newUsername, newInterests, newEmail;
    final static int gallery_pick=1;
    final static String TAG="TAG";
    private DatabaseReference UserRef;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /* Firebase */
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        
        /* Buttons */
        name = (EditText) findViewById(R.id.edit_name);
        newName = name.getText().toString();
        username = (EditText) findViewById(R.id.edit_username);
        newUsername = username.getText().toString();
        interests = (EditText) findViewById(R.id.edit_interests);
        newInterests = interests.getText().toString();
        email = (EditText)findViewById(R.id.edit_email);
        newEmail = email.getText().toString();
        profile_pic = findViewById(R.id.edit_profile_pic);
        update_profile_pic = findViewById(R.id.edit_profile_pic_button);

        update_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pick);

            }
        });

        /* Display profile image */
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.abstract_user_icon).into(profile_pic);

                    newName = dataSnapshot.child("name").getValue().toString();
                    name.setText(newName);

                    newUsername = dataSnapshot.child("username").getValue().toString();
                    username.setText(newUsername);

                    newEmail = dataSnapshot.child("email").getValue().toString();
                    email.setText(newEmail);

                    newInterests = dataSnapshot.child("interests").getValue().toString();
                    interests.setText(newInterests);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submit = findViewById(R.id.edit_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_str = name.getText().toString();
                String username_str = username.getText().toString();
                String interests_str = interests.getText().toString();
                String email_str = email.getText().toString();
                Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);

                intent.putExtra("name", name_str);
//                intent.putExtra("username", username_str);
//                intent.putExtra("interests", interests_str);
//                intent.putExtra("email", email_str);
                /* Save user information */
                UserRef.child("name").setValue(name_str);
                UserRef.child("username").setValue(username_str);
                UserRef.child("email").setValue(email_str);
                UserRef.child("interests").setValue(interests_str);

                startActivity(intent);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG,"on data change");

                if(dataSnapshot.exists()){
                    String image_string = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image_string).placeholder(R.drawable.abstract_user_icon).into(profile_pic);

                    newName = dataSnapshot.child("name").getValue().toString();
                    name.setText(newName);

                    newUsername = dataSnapshot.child("username").getValue().toString();
                    username.setText(newUsername);

                    newEmail = dataSnapshot.child("email").getValue().toString();
                    email.setText(newEmail);

                    newInterests = dataSnapshot.child("interests").getValue().toString();
                    interests.setText(newInterests);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null){
            Uri ImageUri=data.getData();

            final StorageReference filePath=UserProfileImageRef.child(currentUserID+".jpg");
            filePath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            UserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UpdateProfileActivity.this, "Image Stored", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(UpdateProfileActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                    }

                                }

                            });

                        }


                    });
                }
            });
        }
        else{
            Toast.makeText(this,"Error Occured", Toast.LENGTH_SHORT).show();

        }
    }
}
