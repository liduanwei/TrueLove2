package me.himi.love.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName:UserNews
 * @author sparklee liduanwei_911@163.com
 * @date Nov 10, 2014 2:37:08 PM
 */
public class UserNews implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8873445203688340826L;

    private int userId;

    private String faceUrl;

    private int age;

    private int gender;

    private int newsId;

    private String nickname;

    private List<BSImageUrl> imageUrls; // 所有发布心情时上传的图片(大图小图)

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    private String title;

    private String content;

    private String postTime;

    private boolean isPrivate, isAllowComment;

    private String postLongitude, postLatitude, address;

    private int commentsCount;

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public String getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
	this.faceUrl = faceUrl;
    }

    public int getAge() {
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getPostLongitude() {
	return postLongitude;
    }

    public void setPostLongitude(String postLongitude) {
	this.postLongitude = postLongitude;
    }

    public String getPostLatitude() {
	return postLatitude;
    }

    public void setPostLatitude(String postLatitude) {
	this.postLatitude = postLatitude;
    }

    public String getPostTime() {
	return postTime;
    }

    public void setPostTime(String postTime) {
	this.postTime = postTime;
    }

    public int getCommentsCount() {
	return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
	this.commentsCount = commentsCount;
    }

    public int getNewsId() {
	return newsId;
    }

    public void setNewsId(int newsId) {
	this.newsId = newsId;
    }

    public List<BSImageUrl> getImageUrls() {
	return imageUrls;
    }

    public void setImageUrls(List<BSImageUrl> imageUrls) {
	this.imageUrls = imageUrls;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public boolean isPrivate() {
	return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
	this.isPrivate = isPrivate;
    }

    public boolean isAllowComment() {
	return isAllowComment;
    }

    public void setAllowComment(boolean isAllowComment) {
	this.isAllowComment = isAllowComment;
    }

}
