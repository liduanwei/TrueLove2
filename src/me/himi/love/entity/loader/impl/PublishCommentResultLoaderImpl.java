package me.himi.love.entity.loader.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;

import me.himi.love.entity.UserNewsComment;
import me.himi.love.entity.loader.IPublishCommentResultLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:LoginedUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class PublishCommentResultLoaderImpl implements IPublishCommentResultLoader {

    @Override
    public UserNewsComment load(String response) {
	try {
	    JSONObject jsonObj = new JSONObject(response);

	    int status = jsonObj.getInt("status");
	    if (status == 1) {

		JSONObject commentObj = jsonObj.getJSONObject("comment");

		int id = commentObj.getInt("id");
		int userId = commentObj.getInt("user_id");
		String nickname = commentObj.getString("nickname");
		String faceUrl = commentObj.getString("face_url");
		String content = commentObj.getString("content");
		int postTime = commentObj.getInt("post_time");

		UserNewsComment comment = new UserNewsComment();

		String postTimeStr = ActivityUtil.convertTimeToString(postTime * 1000L);

		comment.setNickname(nickname);
		if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		    faceUrl = Constants.HOST + faceUrl.substring(1);
		}
		comment.setFaceUrl(faceUrl);
		comment.setUserId(userId);
		comment.setContent(content);
		comment.setPostTime(postTimeStr);
		comment.setId(id);

		if (commentObj.has("reply_user")) {
		    String replyUserNickname = commentObj.getString("reply_user");
		    comment.setReplyUser(replyUserNickname);

		    String replyUserCommentContent = commentObj.getString("reply_user_content");
		    comment.setReplyUserCommentContent(replyUserCommentContent);
		}
		return comment;

	    }

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }
}
