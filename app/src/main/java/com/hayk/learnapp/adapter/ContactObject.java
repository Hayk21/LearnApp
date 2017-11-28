package com.hayk.learnapp.adapter;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 14.11.2017.
 */

public class ContactObject implements Parcelable {
    private String id;
    private String allName;
    private String name;
    private String lastName;
    private String number;
    private String email;
    private Uri img;

    public ContactObject(String id,String allName,String name,String lastName,String number,String email,Uri img){
        this.id = id;
        this.allName = allName;
        this.name = name;
        this.lastName = lastName;
        this.number = number;
        this.email = email;
        this.img = img;
    }

    protected ContactObject(Parcel in) {
        id = in.readString();
        allName = in.readString();
        name = in.readString();
        lastName = in.readString();
        number = in.readString();
        email = in.readString();
        img = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<ContactObject> CREATOR = new Creator<ContactObject>() {
        @Override
        public ContactObject createFromParcel(Parcel in) {
            return new ContactObject(in);
        }

        @Override
        public ContactObject[] newArray(int size) {
            return new ContactObject[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public String getAllName() {
        return allName;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(allName);
        parcel.writeString(name);
        parcel.writeString(lastName);
        parcel.writeString(number);
        parcel.writeString(email);
        parcel.writeParcelable(img, i);
    }
}
