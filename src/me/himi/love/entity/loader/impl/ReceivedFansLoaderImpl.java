package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.ReceivedFans;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.loader.IReceivedFansLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;

/**
 * @ClassName:ReceivedFansLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 22, 2014 11:53:12 PM
 */
public class ReceivedFansLoaderImpl implements IReceivedFansLoader {
    List<ReceivedFans> fans = new ArrayList<ReceivedFans>();

    @Override
    public List<ReceivedFans> load(String response) {
	// TODO Auto-generated method stub
	fans.clear();
	try {
	    JSONArray jsonArr = new JSONArray(response);
	    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
		JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
		int userId = userJsonObj.getInt("user_id");
		int gender = userJsonObj.getInt("gender");
		int birthday = userJsonObj.getInt("birthday"); // 秒时间
		String nickname = userJsonObj.getString("nickname");
		int height = userJsonObj.getInt("height");
		int weight = userJsonObj.getInt("weight");
		String monthlySalary = userJsonObj.getString("monthly_salary");

		String homeplace = userJsonObj.getString("homeplace");
		String address = userJsonObj.getString("address");

		String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

		ReceivedFans user = new ReceivedFans();
		fans.add(user);

		GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

		GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

		int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

		// 当前月份小于出生月份则表示未满,再-1,不考虑天
		age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

		user.setUserId(userId);
		user.setGender(gender);
		user.setAge(age);
		user.setNickname(nickname);
		user.setHeight(height);
		user.setWeight(weight);
		user.setMonthlySalary(monthlySalary);
		user.setHomeplace(homeplace);
		user.setAddress(address);

		if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		    faceUrl = Constants.HOST + faceUrl.substring(1);
		}

		BSImageUrl bsImageUrl = new BSImageUrl();
		bsImageUrl.setBigImageUrl(faceUrl);
		bsImageUrl.setSmallImageUrl(getSmallImageUrl(faceUrl));
		user.setFaceUrl(bsImageUrl);
	    }

	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return fans;
    }

    public static String getSmallImageUrl(String bigImageUrl) {
	// TODO Auto-generated method stub
	int nameSplitIndex = bigImageUrl.lastIndexOf("/");
	String path = bigImageUrl.substring(0, nameSplitIndex + 1);
	String name = bigImageUrl.substring(nameSplitIndex + 1);
	name = "s_" + name;
	return path + name;
    }

}
