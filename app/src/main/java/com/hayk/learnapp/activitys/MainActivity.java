package com.hayk.learnapp.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForOption;
import com.hayk.learnapp.fragments.AlbumsFragment;
import com.hayk.learnapp.fragments.UsersFragment;

public class MainActivity extends AppCompatActivity implements UsersFragment.UserClickListener{

    public static final String KEY_FOR_LOG = "key_for_log";
    public static final String KEY_FOR_USER_ID = "key_for_user_id";
    public static final String APP_PREF = "settings";
    private static final int USERS_POSITION = 0;
    private static final int PAGE1_POSITION = 1;
    private static final int PAGE2_POSITION = 2;
    public static final int REQUEST_CODE_FOR_LOGIN_ACTIVITY = 1;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private RecyclerView optionsList;
    private AdapterForOption adapterForOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getApplicationContext().getSharedPreferences(APP_PREF, MODE_PRIVATE).getBoolean(KEY_FOR_LOG,false)){
            init();
            setListeners();
        }else {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivityForResult(intent,REQUEST_CODE_FOR_LOGIN_ACTIVITY);
        }

    }

    private void init(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        getSupportActionBar().setTitle("");
        optionsList = (RecyclerView) findViewById(R.id.options_list);
        adapterForOption = new AdapterForOption(MainActivity.this);
        optionsList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        optionsList.setAdapter(adapterForOption);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container,new UsersFragment())
                .commit();
    }

    private void setListeners(){
        adapterForOption.setOnOptionAdapterListener(new AdapterForOption.OnOptionAdapterItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                drawer.closeDrawer(GravityCompat.START);
                switch (position){
                    case USERS_POSITION:
                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.main_container,new UsersFragment())
                                .commit();
                        break;
                }
            }
        });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            init();
            setListeners();
        }else {
            finish();
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

    @Override
    public void onUserClicked(int id) {
        AlbumsFragment albumsFragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_FOR_USER_ID,id);
        albumsFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, albumsFragment)
                .commit();
    }
}
