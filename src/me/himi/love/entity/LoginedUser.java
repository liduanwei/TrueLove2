package me.himi.love.entity;

/**
 * @ClassName:LoginedUser
 * @author sparklee liduanwei_911@163.com
 * @date Nov 8, 2014 6:23:33 PM
 */
public class LoginedUser {
    private int userId;
    private int isVip;
    private int loveMoney;
    private int isLocked;
    private int unlockTime;
    private int gender;

    private int friendsCount, followsCount, fansCount; // 好友数,关注数,粉丝数

    public int getFriendsCount() {
	return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
	this.friendsCount = friendsCount;
    }

    public int getFollowsCount() {
	return followsCount;
    }

    public void setFollowsCount(int followsCount) {
	this.followsCount = followsCount;
    }

    public int getFansCount() {
	return fansCount;
    }

    public void setFansCount(int fansCount) {
	this.fansCount = fansCount;
    }

    private int lastLoginTime;// 上次登录的时间

    private int vipExpireTime;// vip 到期时间

    private String nickname;

    private String faceUrl;

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public int getIsVip() {
	return isVip;
    }

    public void setIsVip(int isVip) {
	this.isVip = isVip;
    }

    public int getLoveMoney() {
	return loveMoney;
    }

    public void setLoveMoney(int loveMoney) {
	this.loveMoney = loveMoney;
    }

    public int getIsLocked() {
	return isLocked;
    }

    public void setIsLocked(int isLocked) {
	this.isLocked = isLocked;
    }

    public int getUnlockTime() {
	return unlockTime;
    }

    public void setUnlockTime(int unlockTime) {
	this.unlockTime = unlockTime;
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

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public int getLastLoginTime() {
	return lastLoginTime;
    }

    public void setLastLoginTime(int lastLoginTime) {
	this.lastLoginTime = lastLoginTime;
    }

    //    private String imUserName, imPassword;

    //    public String getImUserName() {
    //	return imUserName;
    //    }
    //
    //    public void setImUserName(String imUserName) {
    //	this.imUserName = imUserName;
    //    }
    //
    //    public String getImPassword() {
    //	return imPassword;
    //    }
    //
    //    public void setImPassword(String imPassword) {
    //	this.imPassword = imPassword;
    //    }

    public int getVipExpireTime() {
	return vipExpireTime;
    }

    public void setVipExpireTime(int vipExpireTime) {
	this.vipExpireTime = vipExpireTime;
    }

    public DetailInfoUser getDetailInfoUser() {
	return detailInfoUser;
    }

    public void setDetailInfoUser(DetailInfoUser detailInfoUser) {
	this.detailInfoUser = detailInfoUser;
    }

    // 详细资料
    private DetailInfoUser detailInfoUser;
}
