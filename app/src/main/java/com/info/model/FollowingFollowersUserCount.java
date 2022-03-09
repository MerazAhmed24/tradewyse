
package com.info.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowingFollowersUserCount implements Serializable
{

    @SerializedName("followingCount")
    @Expose
    private Integer followingCount;
    @SerializedName("followerCount")
    @Expose
    private Integer followerCount;

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

}
