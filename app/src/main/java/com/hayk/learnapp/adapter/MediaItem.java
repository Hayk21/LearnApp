package com.hayk.learnapp.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 29.11.2017.
 */

public class MediaItem implements Parcelable {
    private String path;
    private String name;
    private byte[] image;
    private boolean isMusic;

    public MediaItem(String path, byte[] image, boolean isMusic) {
        this.path = path;
        name = getFileName(path);
        this.image = image;
        this.isMusic = isMusic;
    }


    protected MediaItem(Parcel in) {
        path = in.readString();
        name = in.readString();
        image = in.createByteArray();
        isMusic = in.readByte() != 0;
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    private String getFileName(String path) {
        int lastPeriodPos = path.lastIndexOf('.');
        return path.substring(0, lastPeriodPos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeByteArray(image);
        parcel.writeByte((byte) (isMusic ? 1 : 0));
    }
}
