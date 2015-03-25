package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.entity.ReceivedSayHi;
import me.himi.love.entity.loader.ISayHiResponseLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:LoginedUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class SayHiResponseLoaderImpl implements ISayHiResponseLoader {

    private List<ReceivedSayHi> sayhiList = new ArrayList<ReceivedSayHi>();

    @Override
    public List<ReceivedSayHi> load(String response) {
	sayhiList.clear();

	System.out.println("sayhi:" + response);
	try {
	    JSONArray hiArray = new JSONArray(response);

	    int len = hiArray.length();
	    for (int i = 0; i < len; ++i) {
		JSONObject sayhi = (JSONObject) hiArray.get(i);
		int userId = sayhi.getInt("user_id");
		int gender = sayhi.getInt("gender");
		String nickname = sayhi.getString("nickname");
		String content = sayhi.getString("sayhi_content");
		int postTime = sayhi.getInt("sayhi_time");
		int isRead = sayhi.getInt("is_read");

		String faceUrl = sayhi.getString("face_url");

		ReceivedSayHi sayhiResponse = new ReceivedSayHi();
		sayhiList.add(sayhiResponse);
		String postTimeStr = ActivityUtil.convertTimeToString(postTime * 1000L);

		sayhiResponse.setGender(gender);
		sayhiResponse.setNickname(nickname);
		if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		    faceUrl = Constants.HOST + faceUrl.substring(1);
		}
		sayhiResponse.setFaceUrl(faceUrl);
		sayhiResponse.setUserId(userId);
		sayhiResponse.setContent(content);
		sayhiResponse.setTimeStr(postTimeStr);
		sayhiResponse.setTime(postTime);
		sayhiResponse.setRead(isRead == 1);

	    }

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return sayhiList;
    }
}
