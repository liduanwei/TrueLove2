package me.himi.love.entity;

import java.io.Serializable;

/**
 * 存放 json 解析用户详细资料 
 * @ClassName:DetailInfoUser
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:42 PM
 */
public class DetailInfoUser implements Serializable {

    private int userId;
    private int gender;
    private int age;
    private String monologue;
    private String nickname;
    private String education;
    private String homeplace;
    private String address;
    private int height;
    private int weight;
    private String distance;
    private String blood;
    private String monthlySalary;
    private String employment;
    private String charmBody;
    private String house;
    private String distanceLove;
    private String oppositeSexType;
    private String premartialSex;
    private String liveWithParents;
    private String wantBaby;
    private String martialStatus;
    private String faceUrl;
    private String interests;
    private String personalities;

    private String constellation; // 星座

    private String lastAddress;// 最后位置

    private int birthdayYear, birthdayMonth, birthdayDay;

    private String lastLogintime, registerTime;

    private String instruction, oftenAddress;// 个人说明,常出没地

    private int loveMoney; // 爱心币

    private boolean isVip;// 是否为vip会员用户

    public String getLastLogintime() {
	return lastLogintime;
    }

    public void setLastLogintime(String lastLogintime) {
	this.lastLogintime = lastLogintime;
    }

    public String getRegisterTime() {
	return registerTime;
    }

    public void setRegisterTime(String registerTime) {
	this.registerTime = registerTime;
    }

    public int getBirthdayYear() {
	return birthdayYear;
    }

    public void setBirthdayYear(int birthdayYear) {
	this.birthdayYear = birthdayYear;
    }

    public int getBirthdayMonth() {
	return birthdayMonth;
    }

    public void setBirthdayMonth(int birthdayMonth) {
	this.birthdayMonth = birthdayMonth;
    }

    public int getBirthdayDay() {
	return birthdayDay;
    }

    public void setBirthdayDay(int birthdayDay) {
	this.birthdayDay = birthdayDay;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public String getMonologue() {
	return monologue;
    }

    public void setMonologue(String monologue) {
	this.monologue = monologue;
    }

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public String getEducation() {
	return education;
    }

    public void setEducation(String education) {
	this.education = education;
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

    public String getBlood() {
	return blood;
    }

    public void setBlood(String blood) {
	this.blood = blood;
    }

    public String getMonthlySalary() {
	return monthlySalary;
    }

    public void setMonthlySalary(String monthlySalary) {
	this.monthlySalary = monthlySalary;
    }

    public String getEmployment() {
	return employment;
    }

    public void setEmployment(String employment) {
	this.employment = employment;
    }

    public String getCharmBody() {
	return charmBody;
    }

    public void setCharmBody(String charmBody) {
	this.charmBody = charmBody;
    }

    public String getHouse() {
	return house;
    }

    public void setHouse(String house) {
	this.house = house;
    }

    public String getDistanceLove() {
	return distanceLove;
    }

    public void setDistanceLove(String distanceLove) {
	this.distanceLove = distanceLove;
    }

    public String getOppositeSexType() {
	return oppositeSexType;
    }

    public void setOppositeSexType(String oppositeSexType) {
	this.oppositeSexType = oppositeSexType;
    }

    public String getPremartialSex() {
	return premartialSex;
    }

    public void setPremartialSex(String premartialSex) {
	this.premartialSex = premartialSex;
    }

    public String getLiveWithParents() {
	return liveWithParents;
    }

    public void setLiveWithParents(String liveWithParents) {
	this.liveWithParents = liveWithParents;
    }

    public String getWantBaby() {
	return wantBaby;
    }

    public void setWantBaby(String wantBaby) {
	this.wantBaby = wantBaby;
    }

    public String getMartialStatus() {
	return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
	this.martialStatus = martialStatus;
    }

    public String getFaceUrl() {
	return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
	this.faceUrl = faceUrl;
    }

    public int getAge() {
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    public String getInterests() {
	return interests;
    }

    public void setInterests(String interests) {
	this.interests = interests;
    }

    public String getPersonalities() {
	return personalities;
    }

    public void setPersonalities(String personalities) {
	this.personalities = personalities;
    }

    @Override
    public String toString() {
	return "age:" + this.age + ",height:" + height + "," + "情感:" + this.martialStatus + "," + this.education + "," + this.distanceLove + "," + this.monthlySalary + "," + this.monologue;
    }

    public String getConstellation() {
	return constellation;
    }

    public void setConstellation(String constellation) {
	this.constellation = constellation;
    }

    public String getImUserName() {
	return imUserName;
    }

    public void setImUserName(String imUserName) {
	this.imUserName = imUserName;
    }

    private String imUserName;

    // 征友设置
    private String wantHomeplace, wantAddress, wantAge, wantHeight, wantEducation, wantMonthSalary, wantAddtional;

    public String getWantHomeplace() {
	return wantHomeplace;
    }

    public void setWantHomeplace(String wantHomeplace) {
	this.wantHomeplace = wantHomeplace;
    }

    public String getWantAddress() {
	return wantAddress;
    }

    public void setWantAddress(String wantAddress) {
	this.wantAddress = wantAddress;
    }

    public String getWantEducation() {
	return wantEducation;
    }

    public void setWantEducation(String wantEducation) {
	this.wantEducation = wantEducation;
    }

    public String getWantMonthSalary() {
	return wantMonthSalary;
    }

    public void setWantMonthSalary(String wantMonthSalary) {
	this.wantMonthSalary = wantMonthSalary;
    }

    public String getWantAddtional() {
	return wantAddtional;
    }

    public void setWantAddtional(String wantAddtional) {
	this.wantAddtional = wantAddtional;
    }

    public String getWantAge() {
	return wantAge;
    }

    public void setWantAge(String wantAge) {
	this.wantAge = wantAge;
    }

    public String getWantHeight() {
	return wantHeight;
    }

    public void setWantHeight(String wantHeight) {
	this.wantHeight = wantHeight;
    }

    public String getDistance() {
	return distance;
    }

    public void setDistance(String distance) {
	this.distance = distance;
    }

    public int getLoveMoney() {
	return loveMoney;
    }

    public void setLoveMoney(int loveMoney) {
	this.loveMoney = loveMoney;
    }

    public String getInstruction() {
	return instruction;
    }

    public void setInstruction(String instruction) {
	this.instruction = instruction;
    }

    public String getOftenAddress() {
	return oftenAddress;
    }

    public void setOftenAddress(String oftenAddress) {
	this.oftenAddress = oftenAddress;
    }

    public boolean isVip() {
	return isVip;
    }

    public void setVip(boolean isVip) {
	this.isVip = isVip;
    }

    public String getLastAddress() {
	return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
	this.lastAddress = lastAddress;
    }

}
