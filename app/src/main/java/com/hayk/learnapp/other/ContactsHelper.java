package com.hayk.learnapp.other;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.hayk.learnapp.adapter.ContactObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 16.11.2017.
 */

public class ContactsHelper {
    private static Context context;
    private ContentResolver contentResolver;
    private String ID,allName, firstName, lastName, number, email, photo;
    private Uri parsedPhoto;
    private List<ContactObject> list = new ArrayList<>();

    public ContactsHelper(Context context) {
        this.context = context;
    }

    public List getContactsList() {
        contentResolver = context.getContentResolver();

        Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (contactsCursor != null && contactsCursor.getCount() > 0) {
            while (contactsCursor.moveToNext()) {
                if(hasEmail(contactsCursor)){
                    getPhoneNumber(contactsCursor);
                    getNameAndPhotoOfUser(contactsCursor);
                    list.add(new ContactObject(ID,allName, firstName, lastName, number, email, parsedPhoto));
                }
            }
            contactsCursor.close();
        }
        return list;
    }

//    public void updateData(Uri uri) throws RemoteException, OperationApplicationException {
//        contentResolver = context.getContentResolver();
//        Cursor idCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,new String[]{ContactsContract.Contacts._ID},ContactsContract.Contacts._ID + " =?",new String[]{"1"},null);
//        String id = null;
//        if (idCursor != null && idCursor.moveToFirst()){
//            id = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
//            idCursor.close();
//        }
//
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
//        contentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS,"098609933");
//
//        ContentValues contentValues1 = new ContentValues();
//        contentValues1.put(ContactsContract.CommonDataKinds.Email.ADDRESS,"example@mail.com");
//        contentValues1.put(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
//
//        String where = ContactsContract.Data._ID + " = " + id;
////        String where1 = ContactsContract.Data._ID + " = ? AND " +
////                ContactsContract.Data.MIMETYPE + " = ?";
////        String[] params2 = new String[] {id,"vnd.android.cursor.item/email_v2"};
////
////        ArrayList<ContentProviderOperation> cpp = new ArrayList<>();
//
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(where,null).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.DATA,"098609933").build());
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(where,null).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Email.ADDRESS,"julik").build());
//
////        contentResolver.update(ContactsContract.Data.CONTENT_URI,contentValues,where,null);
//        contentResolver.update(ContactsContract.Data.CONTENT_URI,contentValues,where,null);
//
//
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
////
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Email.ADDRESS,"Julik").withValue(ContactsContract.CommonDataKinds.Email.TYPE,ContactsContract.CommonDataKinds.Email.TYPE_HOME).withSelection(where,null).build());
////
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,"098609933").withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).withSelection(where,null).build());
//
////        cpp.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
////                .withSelection(where1, params2)
////                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "Hi There")
////                .build());
////
////
////        contentResolver.applyBatch(ContactsContract.AUTHORITY,cpp);
//
//    }




    private boolean hasEmail(Cursor contactsCursor) {
        Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))},
                null);

        if (emailCursor != null && emailCursor.moveToFirst()) {
            ID = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emailCursor.close();
            return true;
        }
        return false;
    }

    private void getPhoneNumber(Cursor contactsCursor){
        Cursor numberCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))},
                null);

        if(numberCursor != null && numberCursor.moveToFirst()){
            number = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numberCursor.close();
        }
    }

    private void getNameAndPhotoOfUser(Cursor contactsCursor){
        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))},
                null);

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
            dataCursor.close();
        }
    }
}
