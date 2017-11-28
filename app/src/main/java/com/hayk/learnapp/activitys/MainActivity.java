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
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForOption;
import com.hayk.learnapp.adapter.ContactObject;
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.fragments.AlbumsFragment;
import com.hayk.learnapp.fragments.ContactsFragment;
import com.hayk.learnapp.fragments.UserPageFragment;
import com.hayk.learnapp.fragments.UsersFragment;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.Utils;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements UsersFragment.UserClickListener, OnCurrentFragmentChangedListener,ContactsFragment.OnContactItemClickedListener {

    public static final String KEY_FOR_LOG = "key_for_log";
    public static final String KEY_FOR_USER_ID = "key_for_user_id";
    public static final String KEY_FOR_CONTACT_OBJECT = "key_for_contact_object";
    public static final String APP_PREF = "settings";
    private static final String USERS_FRAGMENT = "Users";
    private static final String ALBUMS_FRAGMENT = "Albums";
    private static final String CONTACTS_FRAGMENT = "Contacts";
    private static final String USER_PAGE_FRAGMENT = "UserPage";
    private static final int USERS_POSITION = 0;
    private static final int CONTACTS_POSITION = 1;
    private static final int PAGE2_POSITION = 2;
    public static final int REQUEST_CODE_FOR_LOGIN_ACTIVITY = 1;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private AdapterForOption adapterForOption;
    private TextView title;
    Stack<String> titlesStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getApplicationContext().getSharedPreferences(APP_PREF, MODE_PRIVATE).getBoolean(KEY_FOR_LOG, false)) {
            init();
            setListeners();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_FOR_LOGIN_ACTIVITY);
        }

    }

    private void init() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        title = (TextView) findViewById(R.id.activity_title);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        RecyclerView optionsList = (RecyclerView) findViewById(R.id.options_list);
        adapterForOption = new AdapterForOption(MainActivity.this);
        optionsList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        optionsList.setAdapter(adapterForOption);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, new UsersFragment(), USERS_FRAGMENT)
                .commit();
    }

    private void setListeners() {
        adapterForOption.setOnOptionAdapterListener(new AdapterForOption.OnOptionAdapterItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                drawer.closeDrawer(GravityCompat.START);
                switch (position) {
                    case USERS_POSITION:
                        if (!getFragmentManager().findFragmentByTag(USERS_FRAGMENT).isVisible()) {
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_container, new UsersFragment(), USERS_FRAGMENT)
                                    .commit();
                        }
                        break;
                    case CONTACTS_POSITION:
                        getFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.main_container, new ContactsFragment(), CONTACTS_FRAGMENT)
                                .commit();
                        break;
                }
            }
        });

//        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if(getFragmentManager().getBackStackEntryCount() != 0) {
//                    title.setText(getFragmentManager().findFragmentById(R.id.main_container).getTag());
//                }
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            init();
            setListeners();
            if(Utils.getInstance(this).getConnectivity()) {
                DBFunctions.getInstance(this).updateData();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!getFragmentManager().findFragmentById(R.id.main_container).getTag().equals(USERS_FRAGMENT)) {
                super.onBackPressed();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onUserClicked(String id) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, AlbumsFragment.newInstance(id), ALBUMS_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentAttach(String fragmentTitle) {
        titlesStack.push(fragmentTitle);
        title.setText(fragmentTitle);
    }

    @Override
    public void onFragmentDetach() {
        titlesStack.pop();
        if(titlesStack.size() != 0) {
            title.setText(titlesStack.lastElement());
        }
    }

    @Override
    public void onContactItemClicked(ContactObject contactObject) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, UserPageFragment.getInstance(contactObject),USER_PAGE_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }
}
