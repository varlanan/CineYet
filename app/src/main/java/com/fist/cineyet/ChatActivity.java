package com.fist.cineyet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    private DatabaseReference friendsRequestRef;
    String currentUserID;
    Toolbar messageToolBar;
    CircleImageView messageReceiverProfilePicture;
    EditText messageEntry;
    TextView messageReceiverProfileName;
    RecyclerView messageList;

    String receiverID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiverID=getIntent().getExtras().getString("ReceiverID");
        initializeVariables();
        setContentView(R.layout.activity_chat);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("profileimage").getValue()!=null){
                        String image_string=dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(messageReceiverProfilePicture);

                    }
                    if(dataSnapshot.child("name").getValue()!=null){
                        String new_name = dataSnapshot.child("name").getValue().toString();
                        String[] name_split = new_name.split(" ");
                        String uppercase_name = name_split[0].substring(0, 1).toUpperCase() + name_split[0].substring(1).toLowerCase() + " "
                                +  name_split[1].substring(0, 1).toUpperCase() + name_split[1].substring(1).toLowerCase();
                        messageReceiverProfileName.setText(uppercase_name);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initializeVariables(){
        //messageToolBar=findViewById(R.id.message_bar_layout);
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverID);


        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=  (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=   layoutInflater.inflate(R.layout.chat_bar_layout,null);
        actionbar.setCustomView(action_bar_view);
        messageReceiverProfileName=findViewById(R.id.chat_bar_name);
        messageReceiverProfilePicture=findViewById(R.id.chat_bar_profile);
        messageList=findViewById(R.id.message_list);
        messageEntry=findViewById(R.id.message_content);
    }
}
