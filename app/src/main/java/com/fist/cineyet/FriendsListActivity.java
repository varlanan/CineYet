package com.fist.cineyet;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendsListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        BottomNavigationView botNav=findViewById(R.id.friends_nav_bar);
        botNav.setOnNavigationItemSelectedListener(myListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends,new FriendListFragment()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener myListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag=null;
            switch(menuItem.getItemId()){
                case R.id.nav_friends:
                    selectedFrag = new FriendListFragment();
                    break;
                case R.id.nav_requests:
                    selectedFrag = new FriendRequestFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends,selectedFrag).commit();

            return true;

        }
    };
}
