package me.himi.love.entity;

/**
 * 消息中的私信消息
 * @ClassName:PrivateMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class VisitorPrivateMessage extends PrivateMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private String nickname;

    private int lastTime;

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public int getLastTime() {
	return lastTime;
    }

    public void setLastTime(int lastTime) {
	this.lastTime = lastTime;
    }

}
