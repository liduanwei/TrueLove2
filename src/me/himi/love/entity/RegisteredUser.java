package me.himi.love.entity;

/**
 * @ClassName:RegisteredUser
 * @author sparklee liduanwei_911@163.com
 * @date Nov 8, 2014 6:23:33 PM
 */
public class RegisteredUser {
    private int userId;

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public String getUserPassword() {
	return userPassword;
    }

    public void setUserPassword(String userPassword) {
	this.userPassword = userPassword;
    }

    private String userPassword;

}
