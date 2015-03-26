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

    private Gift gift;

    private String fromUserId;
    private String fromUserNickname;
    private String word;

    public Gift getGift() {
	return gift;
    }

    public void setGift(Gift gift) {
	this.gift = gift;
    }

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
}
