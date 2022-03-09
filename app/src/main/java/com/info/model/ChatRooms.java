package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatRooms implements Parcelable {

    private String id;
    private String chatRoomName;
    private Boolean isLocked;

    public ChatRooms() {
    }

    public ChatRooms(String id, String chatRoomName, Boolean isLocked) {
        this.id = id;
        this.chatRoomName = chatRoomName;
        this.isLocked = isLocked;
    }

    protected ChatRooms(Parcel in) {
        id = in.readString();
        chatRoomName = in.readString();
        byte tmpIsLocked = in.readByte();
        isLocked = tmpIsLocked == 0 ? null : tmpIsLocked == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(chatRoomName);
        dest.writeByte((byte) (isLocked == null ? 0 : isLocked ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatRooms> CREATOR = new Creator<ChatRooms>() {
        @Override
        public ChatRooms createFromParcel(Parcel in) {
            return new ChatRooms(in);
        }

        @Override
        public ChatRooms[] newArray(int size) {
            return new ChatRooms[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

}
