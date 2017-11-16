package com.hayk.learnapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {
    private AdapterForContacts adapterForContacts;
    private OnCurrentFragmentChangedListener currentFragmentChangedListener;
    private OnContactItemClickedListener onContactItemClickedListener;

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
        new MyTask().execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //    private void getContactList(Cursor cur) {
//
//        if (cur != null && cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, email + " = ?", new String[]{id}, null);
//                if (emailCursor != null && emailCursor.moveToFirst()) {
//                    String name = ContactsContract.Contacts.DISPLAY_NAME;
//                    String Name = cur.getString(cur.getColumnIndex(
//                            name));
//
//                    String uri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
//                    Uri uri2 = null;
//                    if (uri != null) {
//                        uri2 = Uri.parse(uri);
//                    }
//
//                    list.add(new ContactObject(Name, uri2));
//                }
//                if ((emailCursor != null)) {
//                    emailCursor.close();
//                }
//            }
//        }
//        if (cur != null) {
//            cur.close();
//        }
//    }

    private class MyTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            String allName, firstName, lastName, number, email, photo;
            Uri parsedPhoto;
            List<ContactObject> list = new ArrayList<>();
            String ID = ContactsContract.Contacts._ID;
            ContentResolver cr = getActivity().getContentResolver();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        1);
            } else {
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                if (cur != null && cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        number = null;
                        Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{cur.getString(cur.getColumnIndex(ID))}, null);
                        if (emailCursor != null && emailCursor.moveToFirst()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            Cursor numberCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{cur.getString(cur.getColumnIndex(ID))}, null);
                            if(numberCursor != null && numberCursor.moveToFirst()){
                                number = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                numberCursor.close();
                            }
                            Cursor dataCursor = cr.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{cur.getString(cur.getColumnIndex(ID))}, null);
                            if (dataCursor != null && dataCursor.moveToFirst()) {
                                allName = dataCursor.getString((dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
                                photo = dataCursor.getString((dataCursor.getColumnIndex(ContactsContract.Data.PHOTO_URI)));
                                dataCursor.moveToNext();
                                firstName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA2));
                                lastName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA3));
                                parsedPhoto = null;
                                if (photo != null) {
                                    parsedPhoto = Uri.parse(photo);
                                }
                                list.add(new ContactObject(allName, firstName, lastName, number, email, parsedPhoto));
                                dataCursor.close();
                            }
                            emailCursor.close();
                        }
                    }
                    cur.close();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(Object o) {
            adapterForContacts.updateList((List<ContactObject>) o);
        }
    }

    public interface OnContactItemClickedListener {
        void onContactItemClicked(ContactObject contactObject);
    }
}
