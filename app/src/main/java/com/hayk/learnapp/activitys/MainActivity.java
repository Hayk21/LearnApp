package com.hayk.learnapp.activitys;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForMedias;
import com.hayk.learnapp.adapter.AdapterForOption;
import com.hayk.learnapp.adapter.ContactObject;
import com.hayk.learnapp.adapter.MediaItem;
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.fragments.AlbumsFragment;
import com.hayk.learnapp.fragments.ContactsFragment;
import com.hayk.learnapp.fragments.MediaFragment;
import com.hayk.learnapp.fragments.UserPageFragment;
import com.hayk.learnapp.fragments.UsersFragment;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.Utils;
import com.hayk.learnapp.services.MediaPlayerService;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements UsersFragment.UserClickListener,
        OnCurrentFragmentChangedListener,
        ContactsFragment.OnContactItemClickedListener,
        MediaFragment.OnMediaPlayerListener{

    public static final String KEY_FOR_LOG = "key_for_log";
    public static final String KEY_FOR_USER_ID = "key_for_user_id";
    public static final String KEY_FOR_CONTACT_OBJECT = "key_for_contact_object";
    public static final String APP_PREF = "settings";
    public static final String VIDEO_PATH = "VideoPath";
    private static final String USERS_FRAGMENT = "Users";
    private static final String ALBUMS_FRAGMENT = "Albums";
    private static final String CONTACTS_FRAGMENT = "Contacts";
    private static final String USER_PAGE_FRAGMENT = "UserPage";
    private static final String MEDIA_FRAGMENT = "Media";
    private static final int USERS_POSITION = 0;
    private static final int CONTACTS_POSITION = 1;
    private static final int MEDIA_POSITION = 2;
    public static final int REQUEST_CODE_FOR_LOGIN_ACTIVITY = 1;
    private DrawerLayout drawer;
    private LinearLayout logOut;
    private Toolbar toolbar;
    private AdapterForOption adapterForOption;
    private TextView title;
    private Stack<String> titlesStack = new Stack<>();
    private ServiceConnection serviceConnection;
    private MediaPlayerService mediaPlayerService;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra(MediaPlayerService.MEDIA_ACTION) != null){
            if(!getFragmentManager().findFragmentById(R.id.main_container).getTag().equals(MEDIA_FRAGMENT)) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_container, new MediaFragment(), MEDIA_FRAGMENT)
                        .commit();
                if (mediaPlayerService == null) {
                    bindService(new Intent(MainActivity.this, MediaPlayerService.class), serviceConnection, 0);
                }
            }
        }
    }

    private void init() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mediaPlayerService = ((MediaPlayerService.MediaBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mediaPlayerService = null;
            }
        };
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        title = (TextView) findViewById(R.id.activity_title);
        logOut = (LinearLayout)findViewById(R.id.log_out);
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
                    case MEDIA_POSITION:
                        if (!MediaPlayerService.getIsRunning()) {
                            startService(new Intent(MainActivity.this, MediaPlayerService.class));
                        }
                        if (mediaPlayerService == null) {
                            bindService(new Intent(MainActivity.this, MediaPlayerService.class), serviceConnection, 0);
                        }
                        getFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.main_container,new MediaFragment(),MEDIA_FRAGMENT)
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

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new AlertDialog.Builder(MainActivity.this).setMessage("Are you really want to log out from your account?").setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBFunctions.getInstance(MainActivity.this).clearDatabase();
                        getApplicationContext()
                                .getSharedPreferences(MainActivity.APP_PREF,MODE_PRIVATE)
                                .edit()
                                .putBoolean(MainActivity.KEY_FOR_LOG,false)
                                .apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_FOR_LOGIN_ACTIVITY);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                    }
                }).create().show();
            }
        });

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
    public void onUserClicked(long id) {
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

    @Override
    public void mediaItemClicked(String action, MediaItem mediaItem) {
        switch (action) {
            case AdapterForMedias.PLAY_ACTION:
                if (mediaPlayerService != null) {
                    if (mediaItem.isMusic()) {
                        mediaPlayerService.playFile(mediaItem);
                    } else {
                        mediaPlayerService.stopFile();
                        startActivity(new Intent(MainActivity.this, MediaActivity.class).putExtra(VIDEO_PATH, mediaItem.getPath()));
                    }
                }
                break;
            case AdapterForMedias.PAUSE_ACTION:
                mediaPlayerService.pauseFile();
                break;
            case MediaFragment.MEDIA_FRAGMENT_UNBIND:
                unbindService(serviceConnection);
                mediaPlayerService = null;
                break;
        }
    }
}
