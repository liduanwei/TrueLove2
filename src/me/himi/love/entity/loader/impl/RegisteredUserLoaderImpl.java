package me.himi.love.entity.loader.impl;

import me.himi.love.entity.RegisteredUser;
import me.himi.love.entity.loader.IRegisteredUserLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:LoginedUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class RegisteredUserLoaderImpl implements IRegisteredUserLoader {

    @Override
    public RegisteredUser load(String response) {

	try {
	    JSONObject userJsonObj = new JSONObject(response);

	    int status = userJsonObj.getInt("status");

	    if (0 == status) {// 注册失败
		return null;
	    }

	    int userId = userJsonObj.getInt("user_id");
	    String pwd = userJsonObj.getString("user_password");

	    RegisteredUser user = new RegisteredUser();
	    user.setUserId(userId);
	    user.setUserPassword(pwd);
	    return user;

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }

}
