package me.himi.love.entity;

/**
 * 访客用户
 * @ClassName:VisitorUser
 * @author sparklee liduanwei_911@163.com
 * @date Nov 12, 2014 10:26:11 PM
 */
public class VisitorUser implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6660739939483789906L;
    private int userId;
    private int gender;
    private int age;
    private String nickname;

    private String visitTime; // 访问时间

    private String faceUrl;

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public int getAge() {
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public String getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
	this.faceUrl = faceUrl;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public String getVisitTime() {
	return visitTime;
    }

    public void setVisitTime(String visitTime) {
	this.visitTime = visitTime;
    }

}
