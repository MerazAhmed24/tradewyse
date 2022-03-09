package com.info.interfaces;

/**
 * Created by Amit Gupta on 12,July,2020
 */
public interface TimeLineOnclickListner {

    void positionOfItemClicked(int position);

    void redirectToProfile(String userName, String userId);

    void loadProfileImageByUserName(String userName, int position);
}
