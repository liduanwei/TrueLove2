package me.himi.love.entity;

/**
 * @ClassName:UserPhoto
 * @author sparklee liduanwei_911@163.com
 * @date Nov 11, 2014 12:07:52 PM
 */
public class UserPhoto implements java.io.Serializable {
    private String url;
    private String addtime;
    private String comment;// 用户的注释

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getAddtime() {
	return addtime;
    }

    public void setAddtime(String addtime) {
	this.addtime = addtime;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

}
