package me.himi.love.entity.loader.impl;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.entity.LoadUserNewsComments;
import me.himi.love.entity.UserNewsComment;
import me.himi.love.entity.loader.IUserNewsCommentLoader;
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
public class UserNewsCommentLoaderImpl implements IUserNewsCommentLoader {

    @Override
    public LoadUserNewsComments load(String response) {

	System.out.println("xxxxx comments:" + response);
	try {
	    JSONObject jsonObject = new JSONObject(response);

	    LoadUserNewsComments loadUserNewsComments = new LoadUserNewsComments();
	    int totalCommentCount = jsonObject.getInt("total_comments_count");
	    int newsPostTime = jsonObject.getInt("news_post_time");

	    loadUserNewsComments.setTotalCommentCount(totalCommentCount);
	    loadUserNewsComments.setNewsPostTime(ActivityUtil.convertTimeToString(newsPostTime * 1000L));

	    JSONArray jsonArray = jsonObject.getJSONArray("comments_list");

	    List<UserNewsComment> commentList = new ArrayList<UserNewsComment>();
	    loadUserNewsComments.setComments(commentList);

	    for (int i = 0, n = jsonArray.length(); i < n; ++i) {

		JSONObject jsonObj = jsonArray.getJSONObject(i);

		int id = jsonObj.getInt("id");
		int userId = jsonObj.getInt("user_id");
		String nickname = jsonObj.getString("nickname");
		String faceUrl = jsonObj.getString("face_url");
		String content = jsonObj.getString("content");
		int postTime = jsonObj.getInt("post_time");

		UserNewsComment comment = new UserNewsComment();
		commentList.add(comment);

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

		if (jsonObj.has("reply_user")) {
		    String replyUserNickname = jsonObj.getString("reply_user");
		    comment.setReplyUser(replyUserNickname);
		    // 被回复的用户的评论内容
		    String replayUserCommentContent = jsonObj.getString("reply_user_content");
		    comment.setReplyUserCommentContent(replayUserCommentContent);
		}
	    }

	    return loadUserNewsComments;

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }
}
