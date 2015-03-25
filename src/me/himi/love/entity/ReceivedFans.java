package me.himi.love.entity;

/**
 * @ClassName:Message
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class ReceivedFans implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 531295090454103383L;

    private int gender;
    private String nickname;
    private BSImageUrl faceUrl;
    private String timeStr;

    private int age, height, weight;
    private String monthlySalary, homeplace, address;

    public int getAge() {
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public int getWeight() {
	return weight;
    }

    public void setWeight(int weight) {
	this.weight = weight;
    }

    public String getMonthlySalary() {
	return monthlySalary;
    }

    public void setMonthlySalary(String monthlySalary) {
	this.monthlySalary = monthlySalary;
    }

    public String getHomeplace() {
	return homeplace;
    }

    public void setHomeplace(String homeplace) {
	this.homeplace = homeplace;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    private boolean isRead;

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public BSImageUrl getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(BSImageUrl faceUrl) {
	this.faceUrl = faceUrl;
    }

    public String getTimeStr() {
	return timeStr;
    }

    public void setTimeStr(String timeStr) {
	this.timeStr = timeStr;
    }

    public int getTime() {
	return time;
    }

    public void setTime(int time) {
	this.time = time;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public boolean isRead() {
	return isRead;
    }

    public void setRead(boolean isRead) {
	this.isRead = isRead;
    }

    private int userId;
    private int time;

}
