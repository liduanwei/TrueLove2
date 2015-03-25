package me.himi.love.entity.loader.impl;

import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.loader.ILoginedUserLoader;
import me.himi.love.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * @ClassName:LoginedUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class LoginedUserLoaderImpl implements ILoginedUserLoader {

    @Override
    public LoginedUser load(String response) {

	try {
	    JSONObject userJsonObj = new JSONObject(response);

	    int status = userJsonObj.getInt("status");

	    if (0 == status) {// 登录失败
		return null;
	    }

	    int userId = userJsonObj.getInt("user_id");
	    int gender = userJsonObj.getInt("gender");
	    int isVip = userJsonObj.getInt("is_vip");
	    int loveMoney = userJsonObj.getInt("love_money");
	    int isLocked = userJsonObj.getInt("is_lock");
	    int unlockTime = userJsonObj.getInt("unlock_time");

	    int vipExpireTime = userJsonObj.getInt("vip_expire_time");

	    int friendsCount = userJsonObj.getInt("friends_count");
	    int followsCount = userJsonObj.getInt("follows_count");
	    int fansCount = userJsonObj.getInt("fans_count");

	    String nickname = userJsonObj.getString("nickname");

	    String faceUrl = userJsonObj.getString("face_url");
	    if (faceUrl.startsWith(".")) {
		faceUrl = Constants.HOST + faceUrl.substring(1); //
	    }

	    // im 账户
	    //	    String imUserName = userJsonObj.getString("im_username");
	    //	    String imPassword = userJsonObj.getString("im_password");

//	    Gson gson = new Gson();
//	    LoginedUser user = gson.fromJson(response, LoginedUser.class);

	    LoginedUser user = new LoginedUser();
	    user.setUserId(userId);
	    user.setIsVip(isVip);
	    user.setNickname(nickname);
	    user.setUnlockTime(unlockTime);
	    user.setIsLocked(isLocked);
	    user.setLoveMoney(loveMoney);
	    user.setGender(gender);
	    user.setFaceUrl(faceUrl);

	    //	    user.setImUserName(imUserName);
	    //	    user.setImPassword(imPassword);

	    user.setVipExpireTime(vipExpireTime);

	    user.setFollowsCount(followsCount);
	    user.setFriendsCount(friendsCount);
	    user.setFansCount(fansCount);

	    return user;

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }

}
