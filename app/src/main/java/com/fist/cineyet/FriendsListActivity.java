package com.fist.cineyet;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class FriendsListActivity extends AppCompatActivity {
    String profile_uid;
    Bundle extras;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         extras=getIntent().getExtras();
        profile_uid=extras.getString("UserID");


        setContentView(R.layout.activity_friends_list);
        BottomNavigationView botNav=findViewById(R.id.friends_nav_bar);
        invalidateOptionsMenu();
        MenuItem menuItem=botNav.getMenu().findItem(R.id.nav_requests);
        if(!extras.getString("isPersonalProfile").equals("PERSONAL")){
            menuItem.setTitle("Mutual Friends");
        }
        botNav.setOnNavigationItemSelectedListener(myListener);
        Fragment myfrag=  new FriendListFragment();
        Bundle mybund=new Bundle();
        mybund.putString("UserID",profile_uid);
        myfrag.setArguments(mybund);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends,myfrag).commit();

    }
//    private BottomNavigationView.OnCreateContextMenuListener myList= new BottomNavigationView.OnCreateContextMenuListener(){
//        @Override
//        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//
//        }
//    }
    private BottomNavigationView.OnNavigationItemSelectedListener myListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag=null;
            switch(menuItem.getItemId()){
                case R.id.nav_friends:
                    selectedFrag = new FriendListFragment();
                    Bundle mybund=new Bundle();

                    mybund.putString("UserID",profile_uid);
                    selectedFrag.setArguments(mybund);
                    break;
                case R.id.nav_requests:
                        if(!extras.getString("isPersonalProfile").equals("PERSONAL"))
                            menuItem.setTitle("Mutual Friends");
                        selectedFrag = new FriendRequestFragment();
                        break;


            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_friends,selectedFrag).commit();

            return true;

        }
    };
}
