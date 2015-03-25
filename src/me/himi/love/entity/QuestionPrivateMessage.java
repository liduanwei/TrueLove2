package me.himi.love.entity;

/**
 * 消息中的私信消息
 * @ClassName:PrivateMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class QuestionPrivateMessage extends PrivateMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private int count; // 消息的数量
    private int lastTime;//

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public int getLastTime() {
	return lastTime;
    }

    public void setLastTime(int lastTime) {
	this.lastTime = lastTime;
    }

}
