package com.pdyjak.powerampwearcommon.responses;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Parent implements Parcelable {
    public enum Type {
        Folder("folders"),
        Album("albums"),
        Artist("artists"),
        Queue("queue");

        @NonNull
        public final String value;
        Type(@NonNull String value) {
            this.value = value;
        }
    }

    @NonNull
    public final String id;
    @NonNull
    public final Type type;

    public Parent(@NonNull String id, @NonNull Type type) {
        this.id = id;
        this.type = type;
    }

    public Parent(@NonNull Parcel in) {
        this.id = in.readString();
        this.type = Type.values()[in.readInt()];
    }

    @Override
    public String toString() {
        return type.value + "_" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parent parent = (Parent) o;
        if (!id.equals(parent.id)) return false;
        return type == parent.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(type.ordinal());
    }

    public static final Parcelable.Creator<Parent> CREATOR = new Parcelable.Creator<Parent>() {
        @Override
        public Parent createFromParcel(Parcel source) {
            return new Parent(source);
        }

        @Override
        public Parent[] newArray(int size) {
            return new Parent[size];
        }
    };
}
