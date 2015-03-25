package me.himi.love.entity;

/**
 * 消息中的会话消息
 * @ClassName:ChatMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class SystemMessage extends AbstractMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private String linkUrl; // 点击进入链接地址

    public static enum MessageType {
	NOTIFICATION, NORMAL // 通知消息,普通消息
    }

    private MessageType type; // 消息类型 (点击之后可以转到不同的界面)

    public MessageType getType() {
	return type;
    }

    public void setType(MessageType type) {
	this.type = type;
    }

    public String getLinkUrl() {
	return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
	this.linkUrl = linkUrl;
    }

}
