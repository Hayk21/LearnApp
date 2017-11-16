package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForUsers;
import com.hayk.learnapp.application.AppController;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.rest.User;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;


public class UsersFragment extends Fragment {
    private AdapterForUsers adapterForUsers;
    private UserClickListener userClickListener;
    private SwipeRefreshLayout refresh;
    private OnCurrentFragmentChangedListener onCurrentFragmentChanged;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userClickListener = (UserClickListener) context;
        onCurrentFragmentChanged = (OnCurrentFragmentChangedListener) context;
        onCurrentFragmentChanged.onFragmentAttach("Users");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        userClickListener = null;
        adapterForUsers.setOnAdapterListener(null);
        onCurrentFragmentChanged.onFragmentDetach();
        onCurrentFragmentChanged = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View view){
        refresh = view.findViewById(R.id.swiperefresh);
        RecyclerView listOfUsers = view.findViewById(R.id.users_list);
        adapterForUsers = new AdapterForUsers();
        listOfUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfUsers.setAdapter(adapterForUsers);
        adapterForUsers.setOnAdapterListener(new AdapterForUsers.onAdapterItemClickListener() {
            @Override
            public void onItemClicked(User user) {
                if(userClickListener != null){
                    userClickListener.onUserClicked(user.getId());
                }
            }
        });

        getUsers();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
            }
        });

//        final List<User> list = new ArrayList<>();
//        JsonArrayRequest request = new JsonArrayRequest(RESTHelper.getUsers(), new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    JSONObject jsonObject;
//                    for (int i = 0; i < response.length(); i++) {
//                        GsonBuilder gsonBuilder = new GsonBuilder();
//                        Gson gson = gsonBuilder.create();
//                        list.add(gson.fromJson(response.get(i).toString(),User.class));
////                        user = new User();
////                        user.setId(jsonObject.getInt("id"));
////                        user.setName(jsonObject.getString("name"));
////                        user.setUsername(jsonObject.getString("username"));
////                        user.setEmail(jsonObject.getString("email"));
////                        list.add(user);
//                    }
//
//                    adapterForUsers.addItems(list);
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
//            }
//        });
//        RequestsController.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void getUsers(){
        Call<List<User>> users = AppController.getServerAPI().getUsers();

        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Response<List<User>> response) {
                adapterForUsers.updateList(response.body());
                refresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface UserClickListener {
        void onUserClicked(int id);
    }


}
