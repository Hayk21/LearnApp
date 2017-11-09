package com.hayk.learnapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText mNameEdit;
    EditText mPasswordEdit;
    Button mSignIn;
    boolean mValid = false;
    ConnectivityManager mConManager;
    ImageView mLogo;
    LinearLayout linear;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        linear = (LinearLayout) findViewById(R.id.linear);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mSignIn = (Button) findViewById(R.id.sign_button);
        mLogo = (ImageView) findViewById(R.id.logo);
        mLogo.setColorFilter(getResources().getColor(R.color.white));
        final String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+.+[a-z]+";
        mConManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameEdit.clearFocus();
                mPasswordEdit.clearFocus();
            }
        });

        mNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        i == EditorInfo.IME_ACTION_NEXT) {
                    String mail = mNameEdit.getText().toString();

                    if (!mail.matches(emailPattern) && mail.length()>0) {
                        Toast.makeText(MainActivity.this, "Not valid email", Toast.LENGTH_SHORT).show();
                        mValid = false;
                    } else {
                        mValid = true;
                    }
                }
                return false;
            }
        });

        mNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    String mail = mNameEdit.getText().toString();

                    if (!mail.matches(emailPattern) && mail.length()>0) {
                        Toast.makeText(MainActivity.this, "Not valid email", Toast.LENGTH_SHORT).show();
                        mValid = false;
                    } else {
                        mValid = true;
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            }
        });

        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        i == EditorInfo.IME_ACTION_NEXT) {
                    String password = mPasswordEdit.getText().toString();

                    if (password.length() > 6) {
                        mValid = true;

                    } else if(password.length() <= 6 && password.length() !=0) {
                        Toast.makeText(MainActivity.this, "Not valid password", Toast.LENGTH_SHORT).show();
                        mValid = false;
                    }else {
                        mValid = false;
                    }
                }
                return false;
            }
        });

        mPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    String password = mPasswordEdit.getText().toString();

                    if (password.length() > 6) {
                        mValid = true;

                    } else if(password.length() <= 6 && password.length() !=0) {
                        Toast.makeText(MainActivity.this, "Not valid password", Toast.LENGTH_SHORT).show();
                        mValid = false;
                    }else {
                        mValid = false;
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mValid) {
                    if (mConManager.getActiveNetworkInfo() != null) {
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Please turn on internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
