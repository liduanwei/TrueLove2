package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.loader.IRecommendUserLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:RecommendUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class NearbyUserLoaderImpl implements IRecommendUserLoader {
    private List<NearbyUser> users = new ArrayList<NearbyUser>();

    @Override
    public List<NearbyUser> load(String response) {
	users.clear();
	try {
	    JSONArray jsonArr = new JSONArray(response);
	    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
		JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
		int userId = userJsonObj.getInt("user_id");
		int gender = userJsonObj.getInt("gender");
		int birthday = userJsonObj.getInt("birthday"); // 秒时间

		// vip类型, 主要两大类: 包月(1个月,3个月,6个月),包年
		int isVip = userJsonObj.getInt("is_vip");

		String nickname = userJsonObj.getString("nickname");
		int height = userJsonObj.getInt("height");
		int weight = userJsonObj.getInt("weight");
		String monthlySalary = userJsonObj.getString("monthly_salary");

		String homeplace = userJsonObj.getString("homeplace");
		String address = userJsonObj.getString("address");

		// 兴趣,个性
		String interests = userJsonObj.getString("interests");
		String peronality = userJsonObj.getString("personality");

		// 距离
		String distance = userJsonObj.getString("distance");

		String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

		NearbyUser user = new NearbyUser();
		users.add(user);

		GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

		GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

		int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

		// 当前月份小于出生月份则表示未满,再-1,不考虑天
		age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

		user.setUserId(userId);
		user.setGender(gender);
		user.setAge(age);

		user.setVip(isVip);

		user.setNickname(nickname);
		user.setHeight(height);
		user.setWeight(weight);
		user.setMonthlySalary(monthlySalary);
		user.setHomeplace(homeplace);
		user.setAddress(address);

		user.setInterests(interests);
		user.setPersonality(peronality);

		user.setDistance(distance);

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

	return users;
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
