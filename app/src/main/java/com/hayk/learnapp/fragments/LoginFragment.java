package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hayk.learnapp.R;


public class LoginFragment extends Fragment {

    private EditText nameEdit;
    private EditText passwordEdit;
    private Button signIn;
    private boolean validName = false, validPassword = false;
    final private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+.+[a-z]+";
    private OnLoginEndedListener onLoginEndedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onLoginEndedListener = (OnLoginEndedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onLoginEndedListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setListeners();
    }

    private void init(View view) {
        nameEdit = (EditText) view.findViewById(R.id.edit_name);
        passwordEdit = (EditText) view.findViewById(R.id.edit_password);
        signIn = (Button) view.findViewById(R.id.sign_button);
        ImageView logo = (ImageView) view.findViewById(R.id.logo);
        logo.setColorFilter(getResources().getColor(R.color.white));
    }

    private void setListeners() {
        nameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    String mail = nameEdit.getText().toString();

                    if (!mail.matches(emailPattern) && mail.length() > 0) {
                        Toast.makeText(getActivity(), "Not valid email", Toast.LENGTH_SHORT).show();
                        validName = false;
                    } else {
                        validName = true;
                    }
                }
                return false;
            }
        });

        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    String mail = nameEdit.getText().toString();

                    if (!mail.matches(emailPattern) && mail.length() > 0) {
                        Toast.makeText(getActivity(), "Not valid email", Toast.LENGTH_SHORT).show();
                        validName = false;
                    } else {
                        validName = true;
                    }


                }
            }
        });

        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String password = passwordEdit.getText().toString();

                    if (password.length() > 6) {
                        validPassword = true;

                    } else if (password.length() <= 6 && password.length() != 0) {
                        Toast.makeText(getActivity(), "Not valid password", Toast.LENGTH_SHORT).show();
                        validPassword = false;
                    } else if (password.length() == 0) {
                        validPassword = false;
                    }
                }
                return false;
            }
        });

        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    String password = passwordEdit.getText().toString();

                    if (password.length() > 6) {
                        validPassword = true;

                    } else if (password.length() <= 6 && password.length() != 0) {
                        Toast.makeText(getActivity(), "Not valid password", Toast.LENGTH_SHORT).show();
                        validPassword = false;
                    } else if (password.length() == 0) {
                        validPassword = false;
                    }
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEdit.isFocused() || passwordEdit.isFocused()) {
                    nameEdit.clearFocus();
                    passwordEdit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
                if (((ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
                    if (validName) {
                        if (validPassword) {
                            if (onLoginEndedListener != null) {
                                onLoginEndedListener.loginEnd();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Not valid password", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Not valid email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please turn on internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface OnLoginEndedListener {
        void loginEnd();
    }

}
