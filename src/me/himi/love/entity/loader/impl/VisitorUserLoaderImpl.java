package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import me.himi.love.entity.VisitorUser;
import me.himi.love.entity.loader.IVisitorUserLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VisitorUserLoaderImpl implements IVisitorUserLoader {
    private List<VisitorUser> users = new ArrayList<VisitorUser>();

    @Override
    public List<VisitorUser> load(String response) {
	users.clear();

	try {
	    JSONArray jsonArr = new JSONArray(response);
	    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
		JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
		int userId = userJsonObj.getInt("user_id");
		int gender = userJsonObj.getInt("gender");

		int birthday = userJsonObj.getInt("birthday"); // 秒时间

		String nickname = userJsonObj.getString("nickname");

		String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "";

		VisitorUser user = new VisitorUser();
		users.add(user);

		GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

		GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

		int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

		// 当前月份小于出生月份则表示未满,再-1,不考虑天
		age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

		user.setUserId(userId);
		user.setGender(gender);
		user.setAge(age);
		user.setNickname(nickname);
		user.setFaceUrl(faceUrl);

		if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		    faceUrl = Constants.HOST + faceUrl.substring(1);
		}
		user.setFaceUrl(faceUrl);
		int visitTime = userJsonObj.getInt("visit_time");
		String visitTimeStr = ActivityUtil.convertTimeToString(visitTime * 1000L);
		user.setVisitTime(visitTimeStr);
	    }

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return users;
    }

}
