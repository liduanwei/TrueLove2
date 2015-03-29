package me.himi.love.entity;

/**
 * 消息中的私信消息
 * @ClassName:PrivateMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class PrivateMessage extends AbstractMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    public static enum MessageType {
	SAYHI, QUESTION, FOLLOW, VISITOR, SYSTEM, CHAT, GIFTS // 招呼消息,问题消息,新粉丝消息,新访客消息,系统消息,聊天消息,礼物消息
    }

    private int id; // 本地数据库中的ID
    private int count;// 此类消息的未读数量,用于提示
    private int userId;// 本地记录的所属用户ID

    private int lastMsgTime;

    private MessageType type; // 消息类型 (点击之后可以转到不同的界面)

    public MessageType getType() {
	return type;
    }

    public void setType(MessageType type) {
	this.type = type;
    }

    public int getLastMsgTime() {
	return lastMsgTime;
    }

    public void setLastMsgTime(int lastMsgTime) {
	this.lastMsgTime = lastMsgTime;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

}
