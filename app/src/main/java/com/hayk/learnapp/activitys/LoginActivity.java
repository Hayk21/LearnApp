package com.hayk.learnapp.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hayk.learnapp.R;
import com.hayk.learnapp.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginEndedListener {

    private static final String LOGIN_FRAGMENT = "login_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.login_container,new LoginFragment(),LOGIN_FRAGMENT)
                .commit();
    }

    @Override
    public void loginEnd() {
        getApplicationContext()
                .getSharedPreferences(MainActivity.APP_PREF,MODE_PRIVATE)
                .edit()
                .putBoolean(MainActivity.KEY_FOR_LOG,true)
                .apply();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
