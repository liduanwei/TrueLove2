package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import me.himi.love.entity.UserNews;
import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.loader.IUserNewsLoader;
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
public class UserNewsLoaderImpl implements IUserNewsLoader {

    private List<UserNews> newsList = new ArrayList<UserNews>();

    @Override
    public List<UserNews> load(String response) {
	newsList.clear();

	System.out.println("news:" + response);
	try {
	    JSONObject userJsonObj = new JSONObject(response);

	    int userId = userJsonObj.getInt("user_id");
	    int gender = userJsonObj.getInt("gender");
	    String nickname = userJsonObj.getString("nickname");
	    String faceUrl = userJsonObj.getString("face_url");
	    if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		faceUrl = Constants.HOST + faceUrl.substring(1);
	    }

	    int birthday = userJsonObj.getInt("birthday");

	    JSONArray newsArray = userJsonObj.getJSONArray("news_list");

	    int len = newsArray.length();
	    for (int i = 0; i < len; ++i) {
		JSONObject news = (JSONObject) newsArray.get(i);
		int newsId = news.getInt("id");
		String title = news.getString("title");
		String content = news.getString("content");
		int postTime = news.getInt("post_time");

		int isPrivate = news.getInt("is_private");
		int isAllowComment = news.getInt("is_allow_comment");

		String post_longitude = news.getString("post_longitude");
		String post_latitude = news.getString("post_latitude");

		String address = news.getString("address");

		int commentsCount = news.getInt("comments");

		UserNews un = new UserNews();
		newsList.add(un);
		GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

		GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

		int nowYear = nowCalendar.get(Calendar.YEAR);

		int age = nowYear - birthdayCalendar.get(Calendar.YEAR);
		// 修正 出生月份是否达到
		age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

		// 必须加 L, 否则会被强转成 int 造成变负数
		String postTimeStr = ActivityUtil.convertTimeToString(postTime * 1000L);

		un.setNewsId(newsId);
		un.setAge(age);
		un.setGender(gender);
		un.setNickname(nickname);
		un.setFaceUrl(faceUrl);
		un.setUserId(userId);
		un.setTitle(title);
		un.setContent(content);
		un.setPostTime(postTimeStr);
		un.setPostLongitude(post_longitude);
		un.setPostLatitude(post_latitude);
		un.setAddress(address);

		un.setPrivate(isPrivate == 1);
		un.setAllowComment(isAllowComment == 1);

		un.setCommentsCount(commentsCount);

		un.setImageUrls(new ArrayList<BSImageUrl>());

		for (int t = 1; t <= 10; ++t) {
		    String name = "image_url" + (t <= 9 ? "0" + t : t + "");
		    if (!news.isNull(name)) {
			String bigImageUrl = news.getString(name);
			if (bigImageUrl.startsWith(".")) {
			    bigImageUrl = Constants.HOST + bigImageUrl.substring(1);
			}
			String smallImageUrl = getSmallImageUrl(bigImageUrl);

			//			Strng smallImageUrl = bigImageUrl.substring(bigImageUrl.lastIndexOf("/"))
			BSImageUrl imageUrl = new BSImageUrl();
			imageUrl.setBigImageUrl(bigImageUrl);
			imageUrl.setSmallImageUrl(smallImageUrl);
			un.getImageUrls().add(imageUrl);
			System.out.println("小图:" + smallImageUrl);

		    }
		}
	    }

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return newsList;
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
