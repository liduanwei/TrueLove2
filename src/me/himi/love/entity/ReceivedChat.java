package me.himi.love.entity;

/**
 * @ClassName:Message
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class ReceivedChat implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private String fromUserId;
    private String nickname;
    private BSImageUrl faceUrl;
    private String content;
    private String timeStr;

    private boolean isRead;

    private int count;

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public BSImageUrl getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(BSImageUrl faceUrl) {
	this.faceUrl = faceUrl;
    }

    public String getTimeStr() {
	return timeStr;
    }

    public void setTimeStr(String timeStr) {
	this.timeStr = timeStr;
    }

    public boolean isRead() {
	return isRead;
    }

    public void setRead(boolean isRead) {
	this.isRead = isRead;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public String getFromUserId() {
	return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
	this.fromUserId = fromUserId;
    }

}
