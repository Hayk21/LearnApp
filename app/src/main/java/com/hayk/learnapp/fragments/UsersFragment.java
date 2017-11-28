package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.Utils;


public class UsersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private AdapterForUsers adapterForUsers;
    private UserClickListener userClickListener;
    private SwipeRefreshLayout refresh;
    private OnCurrentFragmentChangedListener onCurrentFragmentChanged;
    private DatabaseUpdatedReceiver databaseUpdatedReceiver = new DatabaseUpdatedReceiver();
    Handler handler;
    Cursor cursor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userClickListener = (UserClickListener) context;
        onCurrentFragmentChanged = (OnCurrentFragmentChangedListener) context;
        onCurrentFragmentChanged.onFragmentAttach("Users");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(databaseUpdatedReceiver,new IntentFilter(DBFunctions.DB_UPDATED_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(databaseUpdatedReceiver);
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

    private void init(View view) {
        refresh = view.findViewById(R.id.swiperefresh);
        RecyclerView listOfUsers = view.findViewById(R.id.users_list);
        adapterForUsers = new AdapterForUsers(getActivity(),null);
        listOfUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfUsers.setAdapter(adapterForUsers);
        getLoaderManager().initLoader(0,null,UsersFragment.this);
        adapterForUsers.setOnAdapterListener(new AdapterForUsers.onAdapterItemClickListener() {
            @Override
            public void onItemClicked(String id) {
                if (userClickListener != null) {
                    userClickListener.onUserClicked(id);
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.getInstance(getActivity()).getConnectivity()) {
                    DBFunctions.getInstance(getActivity()).updateData();
                }else {
                    refresh.setRefreshing(false);
                    Toast.makeText(getActivity(), "Connect to Internet", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new UsersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapterForUsers.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface UserClickListener {
        void onUserClicked(String id);
    }

//    private class UsersGeting extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            return DBFunctions.getInstance(getActivity()).getData(DBHelper.USER_TABLE,null);
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            adapterForUsers.updateList((List<User>) o);
//        }
//    }

    private class DatabaseUpdatedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            refresh.setRefreshing(false);
            getLoaderManager().getLoader(0).forceLoad();
        }
    }

    static private class UsersLoader extends CursorLoader{

        private UsersLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return DBFunctions.getInstance(getContext()).getUsersCursor();
        }
    }

}
