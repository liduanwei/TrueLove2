package me.himi.love.entity;

/**
 * @ClassName:ChatUser
 * @author sparklee liduanwei_911@163.com
 * @date Dec 16, 2014 12:37:15 AM
 */
public class ChatMessage implements java.io.Serializable {
    private int id;
    private String fromUserId;
    private String targetUserId;

    private String avatarUrl;//头像
    private String content;
    private String imageUrl, voiceUrl;
    public static final int SENDING = 0;
    public static final int SEND_SUCCESSED = 1;
    public static final int SEND_FAILED = 2;
    public static final int HISTORY_LOADED = 3;

    private int msgStatus = SENDING;

    private int time;
    private String timeStr;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getFromUserId() {
	return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
	this.fromUserId = fromUserId;
    }

    public String getTargetUserId() {
	return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
	this.targetUserId = targetUserId;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getVoiceUrl() {
	return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
	this.voiceUrl = voiceUrl;
    }

    public int getMsgStatus() {
	return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
	this.msgStatus = msgStatus;
    }

    public String getAvatarUrl() {
	return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
	this.avatarUrl = avatarUrl;
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

}
