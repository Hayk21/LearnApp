package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.adapter.ContactObject;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.squareup.picasso.Picasso;

public class UserPageFragment extends Fragment {
    private OnCurrentFragmentChangedListener onCurrentFragmentChanged;

    public static UserPageFragment getInstance(ContactObject contactObject){
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.KEY_FOR_CONTACT_OBJECT,contactObject);

        UserPageFragment userPageFragment = new UserPageFragment();
        userPageFragment.setArguments(args);

        return userPageFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCurrentFragmentChanged = (OnCurrentFragmentChangedListener) context;
        onCurrentFragmentChanged.onFragmentAttach("");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCurrentFragmentChanged.onFragmentDetach();
        onCurrentFragmentChanged = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView image = view.findViewById(R.id.contact_user_img);
        TextView firstName = view.findViewById(R.id.first_name);
        TextView lastName = view.findViewById(R.id.last_name);
        TextView number = view.findViewById(R.id.phone);
        TextView email = view.findViewById(R.id.email);

        ContactObject contactObject = getArguments().getParcelable(MainActivity.KEY_FOR_CONTACT_OBJECT);

        if(contactObject.getImg() != null){
            Picasso.with(getActivity()).load(contactObject.getImg()).into(image);
        }
        firstName.setText(contactObject.getName());
        lastName.setText(contactObject.getLastName());
        number.setText(contactObject.getNumber());
        email.setText(contactObject.getEmail());
    }
}
