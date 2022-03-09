package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amit Gupta on 15,July,2020
 */
public class FaqAnswersModels implements Parcelable {
    private String answer;

    public FaqAnswersModels(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    protected FaqAnswersModels(Parcel in) {
        answer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(answer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FaqAnswersModels> CREATOR = new Creator<FaqAnswersModels>() {
        @Override
        public FaqAnswersModels createFromParcel(Parcel in) {
            return new FaqAnswersModels(in);
        }

        @Override
        public FaqAnswersModels[] newArray(int size) {
            return new FaqAnswersModels[size];
        }
    };
}
