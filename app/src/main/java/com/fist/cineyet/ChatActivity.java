package com.fist.cineyet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import  java.sql.Timestamp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    private DatabaseReference messageRef;
    String currentUserID;
    Toolbar messageToolBar;
    CircleImageView messageReceiverProfilePicture;
    EditText messageEntry;
    TextView messageReceiverProfileName;
    RecyclerView messageList;
    ImageButton messageSendButton;

    String receiverID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeVariables();
        displayReceiverInfo();
        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

    }
    private void SendMessage() {
        String messageText=messageEntry.getText().toString();
        if(messageText.equals(""))
            Toast.makeText(ChatActivity.this,"Please Enter Text!",Toast.LENGTH_SHORT).show();
        else{

            DatabaseReference message_key= messageRef.child(currentUserID).child(receiverID).push();
            String message_key_string=message_key.getKey();
            String senderReference=currentUserID+"/"+receiverID+"/"+message_key_string;
            String receiverReference=receiverID+"/"+currentUserID+"/"+message_key_string;

            //date
            //      Calendar date = Calendar.getInstance();
            final String absoluteTimeString = String.valueOf(System.currentTimeMillis());;
//            SimpleDateFormat current_date = new SimpleDateFormat("dd-MM-YYYY");
//            final String saveCurrentDate = current_date.format(date.getTime());
//
//            //time
//            Calendar time = Calendar.getInstance();
//            SimpleDateFormat current_time = new SimpleDateFormat("HH:mm:ss");
//            final String saveCurrentTime = current_time.format(date.getTime());

            HashMap messageContent=new HashMap();
            messageContent.put("message",messageText);
            messageContent.put("time",absoluteTimeString);
            messageContent.put("from",currentUserID);
            messageContent.put("type","text"); //modify later when send pictures enabled

            HashMap messageBody= new HashMap();
            messageBody.put(senderReference,messageContent);
            messageBody.put(receiverReference,messageContent);
            messageRef.updateChildren(messageBody).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    messageEntry.setText("");
                }
            });

        }


    }

    private void initializeVariables(){
        //messageToolBar=findViewById(R.id.message_bar_layout);
        myFirebaseAuth = FirebaseAuth.getInstance();
        receiverID=getIntent().getExtras().getString("ReceiverID");
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverID);
        messageRef=FirebaseDatabase.getInstance().getReference().child("Messages");
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
        messageSendButton=findViewById(R.id.message_send_button);

    }
    private void displayReceiverInfo(){

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
}