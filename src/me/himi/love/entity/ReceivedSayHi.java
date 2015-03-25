package me.himi.love.entity;

/**
 * @ClassName:Message
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class ReceivedSayHi implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private int gender;
    private String nickname;
    private String faceUrl;
    private String timeStr;

    private boolean isRead;

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public String getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
	this.faceUrl = faceUrl;
    }

    public String getTimeStr() {
	return timeStr;
    }

    public void setTimeStr(String timeStr) {
	this.timeStr = timeStr;
    }

    public int getTime() {
	return time;
    }

    public void setTime(int time) {
	this.time = time;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public boolean isRead() {
	return isRead;
    }

    public void setRead(boolean isRead) {
	this.isRead = isRead;
    }

    private String content;
    private int userId;
    private int time;

}
