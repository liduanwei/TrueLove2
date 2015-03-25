package me.himi.love.entity;

/**
 * 基本消息
 * @ClassName:AbstractMessage
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public abstract class AbstractMessage implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;
    private boolean isReaded;

//    private Object obj;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    public String getTime() {
	return time;
    }

    public void setTime(String time) {
	this.time = time;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

//    public Object getObj() {
//	return obj;
//    }
//
//    public void setObj(Object obj) {
//	this.obj = obj;
//    }

    public boolean isReaded() {
	return isReaded;
    }

    public void setReaded(boolean isReaded) {
	this.isReaded = isReaded;
    }

    private String title;
    private String content;
    private String icon;
    private String time;

}
