package com.example.sports;

import android.os.Parcel;
import android.os.Parcelable;

public class Sport implements Parcelable {
    private final String name;
    private final String info;
    private final int image;
    Sport(String name, String info, int image){
        this.name = name;
        this.info = info;
        this.image = image;
    }

    protected Sport(Parcel in) {
        name = in.readString();
        info = in.readString();
        image = in.readInt();
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public int getImage(){
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(info);
        parcel.writeInt(image);
    }
}
