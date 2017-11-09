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
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hayk.learnapp.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

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
    LoginFragment loginFragment;

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


//        Menu menu = navigationView.getMenu();
//
//        // find MenuItem you want to change
//        MenuItem nav_camara = menu.findItem(R.id.nav_camera);
//
//        // set new title to the MenuItem
//        nav_camara.setTitle("NewTitleForCamera");
//
//        // do the same for other MenuItems
//        MenuItem nav_gallery = menu.findItem(R.id.nav_gallery);
//        nav_gallery.setTitle("NewTitleForGallery");



        navigationView.setNavigationItemSelectedListener(this);
        init();
    }

    private void init(){
        container = (LinearLayout)findViewById(R.id.main_container);
        loginPref = getPreferences(MODE_PRIVATE);
        fragmentManager = getFragmentManager();
        actionBarSetListener = new LoginFragment.onActionBarSetListener() {
            @Override
            public void setActionBar() {
                getSupportActionBar().show();
                drawer.addView(navigationView);
                toggle.setDrawerIndicatorEnabled(true);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(loginFragment);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
