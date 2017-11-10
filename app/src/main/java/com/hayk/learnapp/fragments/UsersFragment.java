package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForUsers;
import com.hayk.learnapp.rest.ServerAPI;
import com.hayk.learnapp.rest.User;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class UsersFragment extends Fragment {
    RecyclerView listOfUsers;
    RecyclerView.LayoutManager manager;
    AdapterForUsers adapterForUsers;
    usersFragmentEventListener eventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    private void init(){
        listOfUsers = (RecyclerView)getActivity().findViewById(R.id.users_list);
        manager = new LinearLayoutManager(getActivity());
        adapterForUsers = new AdapterForUsers();
        listOfUsers.setLayoutManager(manager);
        listOfUsers.setAdapter(adapterForUsers);
        adapterForUsers.setOnAdapterListener(new AdapterForUsers.onAdapterItemClickListener() {
            @Override
            public void onItemClicked(User user) {
                if(eventListener != null){
                    eventListener.usersEvent();
                }
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI serverAPI = retrofit.create(ServerAPI.class);

        Call<List<User>> users = serverAPI.getUsers();

        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Response<List<User>> response) {
                adapterForUsers.addItems(response.body());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public interface usersFragmentEventListener{
        void usersEvent();
    }

    public void setOnUsersFragmentEventListener(usersFragmentEventListener eventListener){
        this.eventListener = eventListener;
    }

}
