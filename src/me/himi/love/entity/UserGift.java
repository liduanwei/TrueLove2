package me.himi.love.entity;

/**
 * 
 * @ClassName:UserGift
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class UserGift implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String id;

    public String getGiftName() {
	return giftName;
    }

    public void setGiftName(String giftName) {
	this.giftName = giftName;
    }

    public String getGiftImageUrl() {
	return giftImageUrl;
    }

    public void setGiftImageUrl(String giftImageUrl) {
	this.giftImageUrl = giftImageUrl;
    }

    private String giftName, giftImageUrl;

    private String fromUserId;
    private String fromUserNickname;
    private String fromUserAvatar;
    private String word;

    private int addTime;

    public String getFromUserId() {
	return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
	this.fromUserId = fromUserId;
    }

    public String getFromUserNickname() {
	return fromUserNickname;
    }

    public void setFromUserNickname(String fromUserNickname) {
	this.fromUserNickname = fromUserNickname;
    }

    public String getWord() {
	return word;
    }

    public void setWord(String word) {
	this.word = word;
    }

    public String getFromUserAvatar() {
	return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
	this.fromUserAvatar = fromUserAvatar;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public int getAddTime() {
	return addTime;
    }

    public void setAddTime(int addTime) {
	this.addTime = addTime;
    }
}
