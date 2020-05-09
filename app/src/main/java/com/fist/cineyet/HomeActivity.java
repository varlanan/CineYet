package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
//    private FirebaseAuth myFirebaseAuth;
//    private DatabaseReference userRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        BottomNavigationView botNav=findViewById(R.id.bottom_nav);
        botNav.setOnNavigationItemSelectedListener(myListener);

//        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent intent=getIntent();
        if(intent.hasExtra("name")){
            Fragment profFrag=new profile_page();
            Bundle mybund=new Bundle();
            mybund.putString("isPersonalProfile","PERSONAL");
            profFrag.setArguments(mybund);
            botNav.setSelectedItemId(R.id.nav_profile);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,profFrag).commit();

        }
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener myListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag=null;
            switch(menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFrag = new HomeFragment();
                    break;
                case R.id.nav_hot:
                    selectedFrag = new HotFragment();
                    break;
                case R.id.nav_profile:
                    Bundle myBund = new Bundle();
                    myBund.putString("isPersonalProfile","PERSONAL");
                    selectedFrag = new profile_page();
                    selectedFrag.setArguments(myBund);
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFrag).commit();

            return true;

        }
    };

//    @Override
//    protected void onStart(){
//        super.onStart();
//        setContentView(R.layout.activity_home_page);
//
//        myFirebaseAuth = FirebaseAuth.getInstance();
//
//        FirebaseUser currentUser = myFirebaseAuth.getCurrentUser();
//        if(currentUser != null){
//            final String current_user_id = myFirebaseAuth.getCurrentUser().getUid();
//            userRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.hasChild(current_user_id)){
//                        /* Send user to setup activity */
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

    /* Ensures user does not return to log in screen after logging in the first time and being redirected to the home page*/
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


