package com.example.javenture;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class ImageItem implements Parcelable {
    private Uri localUri;
    private String remoteUrl;

    public ImageItem(Uri localUri) {
        this.localUri = localUri;
        this.remoteUrl = null;
    }

    public ImageItem(String remoteUrl) {
        this.remoteUrl = remoteUrl;
        this.localUri = null;
    }

    public ImageItem(Parcel in) {
        localUri = in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        remoteUrl = in.readString();
    }

    public Uri getLocalUri() {
        return localUri;
    }

    public void setLocalUri(Uri localUri) {
        // ignore if remote url is already set
        if (this.remoteUrl != null) {
            return;
        }
        this.localUri = localUri;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        if (this.localUri != null) {
            // remove local uri if remote url is set
            this.localUri = null;
        }
        this.remoteUrl = remoteUrl;
    }

    public boolean isLocal() {
        return localUri != null && remoteUrl == null;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(localUri, i);
        parcel.writeString(remoteUrl);
    }
}
