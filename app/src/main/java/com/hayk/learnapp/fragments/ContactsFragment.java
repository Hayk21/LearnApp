package com.hayk.learnapp.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForContacts;
import com.hayk.learnapp.adapter.ContactObject;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.ContactsHelper;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {
    private static final int CONTACTS_PERMISION_REQUEST = 1;
    private AdapterForContacts adapterForContacts;
    private OnCurrentFragmentChangedListener currentFragmentChangedListener;
    private OnContactItemClickedListener onContactItemClickedListener;
    List<ContactObject> list = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentFragmentChangedListener = (OnCurrentFragmentChangedListener) context;
        onContactItemClickedListener = (OnContactItemClickedListener) context;
        currentFragmentChangedListener.onFragmentAttach("Contacts");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        currentFragmentChangedListener.onFragmentDetach();
        currentFragmentChangedListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView listContacts = view.findViewById(R.id.contacts_list);
        listContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterForContacts = new AdapterForContacts(getActivity());
        adapterForContacts.setOnContactAdapterListener(new AdapterForContacts.OnContactAdapterItemClickListener() {
            @Override
            public void onItemClicked(ContactObject contactObject) {
                onContactItemClickedListener.onContactItemClicked(contactObject);
            }
        });
        listContacts.setAdapter(adapterForContacts);
        if(takePermision()) {
            new ContactsGiving().execute();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private boolean takePermision() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        CONTACTS_PERMISION_REQUEST);
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                new ContactsGiving().execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ContactsGiving extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            return new ContactsHelper(getActivity()).getContactsList();
        }

        @Override
        protected void onPostExecute(Object o) {
            list = ((List<ContactObject>)o);
            adapterForContacts.updateList((List<ContactObject>) o);
//            String name = "Ani";
//            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,name);
//            try {
//                new ContactsHelper(getActivity()).updateData(uri);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            } catch (OperationApplicationException e) {
//                e.printStackTrace();
//            }
        }
    }

    public interface OnContactItemClickedListener {
        void onContactItemClicked(ContactObject contactObject);
    }
}
