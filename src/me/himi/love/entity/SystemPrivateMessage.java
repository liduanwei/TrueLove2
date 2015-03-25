package me.himi.love.entity;

/**
 * 消息中的私信消息
 * @ClassName:PrivateMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class SystemPrivateMessage extends PrivateMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private String nickname;
    private int userId;

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

}
