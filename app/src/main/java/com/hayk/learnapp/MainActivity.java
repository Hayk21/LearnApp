package com.hayk.learnapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.hayk.learnapp.fragments.AlbumsFragment;
import com.hayk.learnapp.fragments.LoginFragment;
import com.hayk.learnapp.fragments.UsersFragment;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_FOR_LOG = "key_for_log";
    private LinearLayout container;
    private SharedPreferences loginPref;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LoginFragment.onActionBarSetListener actionBarSetListener;
    UsersFragment.usersFragmentEventListener eventListener;
    LoginFragment loginFragment;
    LinearLayout usersItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        init();
        setListeners();
    }

    private void init(){
        container = (LinearLayout)findViewById(R.id.main_container);
        usersItem = (LinearLayout)findViewById(R.id.users_item);
        loginPref = getPreferences(MODE_PRIVATE);
        fragmentManager = getFragmentManager();
        getSupportActionBar().setTitle("");
        actionBarSetListener = new LoginFragment.onActionBarSetListener() {
            @Override
            public void setActionBar() {
                getSupportActionBar().show();
                getSupportActionBar().setTitle("");
                drawer.addView(navigationView);
                toggle.setDrawerIndicatorEnabled(true);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(loginFragment);
                fragmentTransaction.commit();
            }
        };
        eventListener = new UsersFragment.usersFragmentEventListener() {
            @Override
            public void usersEvent() {
                AlbumsFragment albumsFragment = new AlbumsFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container,albumsFragment);
                fragmentTransaction.commit();
            }
        };
        if(loginPref.getBoolean(KEY_FOR_LOG,false)){

        }else {
            toggle.setDrawerIndicatorEnabled(false);
            drawer.removeView(navigationView);
            getSupportActionBar().hide();
            loginFragment = new LoginFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_container,loginFragment);
            fragmentTransaction.commit();
            loginFragment.setActionListener(actionBarSetListener);
        }
    }

    private void setListeners(){
        usersItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
                UsersFragment usersFragment = new UsersFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.main_container,usersFragment);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
