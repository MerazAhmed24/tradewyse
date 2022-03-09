package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialComment implements Parcelable {

    private String documentReferenceId;
    private String authorname;
    private Long date;
    private String title;

    public SocialComment(String documentReferenceId, Long date, String authorName, String title) {
        this.documentReferenceId = documentReferenceId;
        this.date = date;
        this.authorname = authorName;
        this.title = title;
    }

    protected SocialComment(Parcel in) {
        documentReferenceId = in.readString();
        authorname = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentReferenceId);
        dest.writeString(authorname);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocialComment> CREATOR = new Creator<SocialComment>() {
        @Override
        public SocialComment createFromParcel(Parcel in) {
            return new SocialComment(in);
        }

        @Override
        public SocialComment[] newArray(int size) {
            return new SocialComment[size];
        }
    };

    public String getAuthorName() {
        return authorname;
    }

    public void setAuthorName(String authorName) {
        this.authorname = authorName;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentReferenceId() {
        return documentReferenceId;
    }

    public void setDocumentReferenceId(String documentReferenceId) {
        this.documentReferenceId = documentReferenceId;
    }
}
