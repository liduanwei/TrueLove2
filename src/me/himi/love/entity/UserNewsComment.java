package me.himi.love.entity;

import java.io.Serializable;

/**
 * @ClassName:UserNews
 * @author sparklee liduanwei_911@163.com
 * @date Nov 10, 2014 2:37:08 PM
 */
public class UserNewsComment implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8873445203688340826L;

    private int userId;

    private String faceUrl;

    private String nickname;

    private String replyUser;

    private String replyUserCommentContent; //被回复的用户的评论的内容

    private int id;

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    private String content;

    private String postTime;

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

    public String getPostTime() {
	return postTime;
    }

    public void setPostTime(String postTime) {
	this.postTime = postTime;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getReplyUser() {
	return replyUser;
    }

    public void setReplyUser(String replyUser) {
	this.replyUser = replyUser;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getReplyUserCommentContent() {
	return replyUserCommentContent;
    }

    public void setReplyUserCommentContent(String replyUserCommentContent) {
	this.replyUserCommentContent = replyUserCommentContent;
    }

}
