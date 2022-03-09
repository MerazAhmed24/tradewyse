package com.info.model

import android.net.Uri
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



public class FollowUserModel {

    @SerializedName("id")
    @Expose
    private var id: String? = null
    @SerializedName("userId")
    @Expose
    private var userId: String? = null
    @SerializedName("userName")
    @Expose
    private var userName: String? = null
    @SerializedName("followUserId")
    @Expose
    private var followUserId: String? = null
    @SerializedName("followUserName")
    @Expose
    private var followUserName: String? = null
    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("description")
    @Expose
    private var description: String? = null
    @SerializedName("createdOn")
    @Expose
    private var createdOn: Long? = null
    @SerializedName("modifiedOn")
    @Expose
    private var modifiedOn: Long? = null
    @SerializedName("follow")
    @Expose
    private var follow: Boolean? = null


    @SerializedName("loginUserFollow")
    @Expose
    private var loginUserFollow: Boolean? = null



    private var uri: Uri? = null



    fun getLoginUserFollow(): Boolean? {
        return loginUserFollow
    }

    fun setLoginUserFollow(isFollow: Boolean) {
        this.loginUserFollow = isFollow
    }


    fun getUri(): Uri? {
        return uri
    }

    fun setUri(id: Uri) {
        this.uri = id
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun getFollowUserId(): String? {
        return followUserId
    }

    fun setFollowUserId(followUserId: String) {
        this.followUserId = followUserId
    }

    fun getFollowUserName(): String? {
        return followUserName
    }

    fun setFollowUserName(followUserName: String) {
        this.followUserName = followUserName
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getCreatedOn(): Long? {
        return createdOn
    }

    fun setCreatedOn(createdOn: Long?) {
        this.createdOn = createdOn
    }

    fun getModifiedOn(): Long? {
        return modifiedOn
    }

    fun setModifiedOn(modifiedOn: Long?) {
        this.modifiedOn = modifiedOn
    }

    fun getFollow(): Boolean? {
        return follow
    }

    fun setFollow(follow: Boolean?) {
        this.follow = follow
    }


}