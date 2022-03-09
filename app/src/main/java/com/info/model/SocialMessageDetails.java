package com.info.model;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocialMessageDetails implements Serializable {

    private String id;
    private String authorName;
    private String commentBody;
    private Long publishDateTime;
    private String socialSiteName;
    private String stockSymbol;
    private String stockName;
    private Long modifiedOn;
    private Long createdOn;
    private String url;
    private String description;
    private String radditUrlName;
    private List<SocialComment> comment;
    private String twitterName;
    private String mentions;
    private String photos;
    private String repliesCount;
    private String retweetsCount;
    private String likesCount;
    private String hashtags;
    private String cashtags;
    private String link;
    private String quoteUrl;
    private String video;
    private String replyTo;
    private String thumbnail;
    private Object channelNameDiscord;
    private Object totalChannelDiscord;
    private Integer totalComment;
    private String documentReferenceId;
    private String profileImage;
    private String messageType;
    private String caption;
    private int likeCount = 0;
    private List<Map<String, Object>> likedByUserName;

    public SocialMessageDetails(String documentReferenceId, Long createdOn, String authorName, String commentBody, String description,
                                String id, String link, Long modifiedOn, Long publishDateTime, String socialSiteName, String stockName,
                                String stockSymbol, String profileImage, String url, String radditUrlName, List<SocialComment> comment,
                                String messageType, String caption, int likeCount, List<Map<String, Object>> likedByUserName) {
        this.documentReferenceId = documentReferenceId;
        this.createdOn = createdOn;
        this.authorName = authorName;
        this.commentBody = commentBody;
        this.description = description;
        this.id = id;
        this.link = link;
        this.modifiedOn = modifiedOn;
        this.publishDateTime = publishDateTime;
        this.socialSiteName = socialSiteName;
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
        this.profileImage = profileImage;
        this.url = url;
        this.radditUrlName = radditUrlName;
        this.comment = comment;
        this.messageType = messageType;
        this.caption = caption;
        this.likeCount = likeCount;
        this.likedByUserName = likedByUserName;
    }

    public SocialMessageDetails() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public Long getPublishDateTime() {
        return publishDateTime;
    }

    public void setPublishDateTime(Long publishDateTime) {
        this.publishDateTime = publishDateTime;
    }

    public String getSocialSiteName() {
        return socialSiteName;
    }

    public void setSocialSiteName(String socialSiteName) {
        this.socialSiteName = socialSiteName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRadditUrlName() {
        return radditUrlName;
    }

    public void setRadditUrlName(String radditUrlName) {
        this.radditUrlName = radditUrlName;
    }

    public List<SocialComment> getComment() {
        return comment;
    }

    public void setComment(List<SocialComment> comment) {
        this.comment = comment;
    }

    public String getTwitterName() {
        return twitterName;
    }

    public void setTwitterName(String twitterName) {
        this.twitterName = twitterName;
    }

    public String getMentions() {
        return mentions;
    }

    public void setMentions(String mentions) {
        this.mentions = mentions;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(String repliesCount) {
        this.repliesCount = repliesCount;
    }

    public String getRetweetsCount() {
        return retweetsCount;
    }

    public void setRetweetsCount(String retweetsCount) {
        this.retweetsCount = retweetsCount;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getCashtags() {
        return cashtags;
    }

    public void setCashtags(String cashtags) {
        this.cashtags = cashtags;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQuoteUrl() {
        return quoteUrl;
    }

    public void setQuoteUrl(String quoteUrl) {
        this.quoteUrl = quoteUrl;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getChannelNameDiscord() {
        return channelNameDiscord;
    }

    public void setChannelNameDiscord(Object channelNameDiscord) {
        this.channelNameDiscord = channelNameDiscord;
    }

    public Object getTotalChannelDiscord() {
        return totalChannelDiscord;
    }

    public void setTotalChannelDiscord(Object totalChannelDiscord) {
        this.totalChannelDiscord = totalChannelDiscord;
    }

    public Integer getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment) {
        this.totalComment = totalComment;
    }

    public String getDocumentReferenceId() {
        return documentReferenceId;
    }

    public void setDocumentReferenceId(String documentReferenceId) {
        this.documentReferenceId = documentReferenceId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<Map<String, Object>> getLikedByUserName() {
        return likedByUserName;
    }

    public void setLikedByUserName(List<Map<String, Object>> likedByUserName) {
        this.likedByUserName = likedByUserName;
    }
}