package me.himi.love;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.ChatMessage;
import me.himi.love.entity.ReceivedChat;
import me.himi.love.entity.loader.impl.UserNewsLoaderImpl;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:AppServiceChatImpl
 * @author sparklee liduanwei_911@163.com
 * @date Dec 16, 2014 4:19:26 PM
 */
public class AppServiceChatImpl implements IAppServiceChat {

    private static IAppServiceChat instance;

    private AppServiceChatImpl() {

    }

    public synchronized static IAppServiceChat getInstance() {
	if (null == instance) {
	    instance = new AppServiceChatImpl();
	}
	return instance;
    }

    @Override
    public void sendMessage(final SendMessagePostParams postParams, final OnSendMessageResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_SEND_CHATMSG;
	RequestParams params = new RequestParams();
	params.put("to_user_id", postParams.toUserId);
	params.put("content", postParams.content);
	params.put("image_url", postParams.imageUrl);
	params.put("voice_url", postParams.voiceUrl);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("发送聊天消息:" + res);

		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    ChatMessage msg = new ChatMessage();
		    int id = jsonObj.getInt("id");

		    String face = jsonObj.getString("face");
		    String content = jsonObj.getString("content");
		    String imageUrl = jsonObj.getString("image_url");
		    String voiceUrl = jsonObj.getString("voice_url");
		    msg.setId(id);

		    if (face.startsWith(".")) {
			face = Constants.HOST + face.substring(1);
		    }
		    msg.setAvatarUrl(face);
		    msg.setFromUserId(postParams.fromUserId);
		    msg.setTargetUserId(postParams.toUserId);
		    msg.setContent(content);
		    msg.setImageUrl(imageUrl);
		    msg.setVoiceUrl(voiceUrl);

		    listener.onSuccess(msg);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void loadAllChatMessages(LoadAllChatMessagesPostParams postParams, final OnLoadAllChatMessagesResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_LAST_CHATMSGES;
	RequestParams params = new RequestParams();
	params.put("page", postParams.page);
	params.put("page_size", postParams.pageSize);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("获取所有不同用户最新的一条聊天消息:" + res);

		try {
		    JSONArray jsonArr = new JSONArray(res);
		    int length = jsonArr.length();
		    List<ReceivedChat> chats = new ArrayList<ReceivedChat>();
		    for (int i = 0, n = length; i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			String userId = jsonObj.getString("user_id");
			String nickname = jsonObj.getString("nickname");
			String face = jsonObj.getString("face");
			String content = jsonObj.getString("content");
			int time = jsonObj.getInt("time");
			int count = jsonObj.getInt("count");

			ReceivedChat chat = new ReceivedChat();
			chats.add(chat);
			chat.setFromUserId(userId);
			chat.setNickname(nickname);

			chat.setContent(content);
			chat.setCount(count);
			chat.setTimeStr(ActivityUtil.convertTimeToString(time * 1000L));

			BSImageUrl faceUrl = new BSImageUrl();
			if (face.startsWith(".")) {
			    face = Constants.HOST + face.substring(1);
			}
			faceUrl.setBigImageUrl(face);
			faceUrl.setSmallImageUrl(UserNewsLoaderImpl.getSmallImageUrl(face));

			chat.setFaceUrl(faceUrl);

		    }

		    listener.onSuccess(chats);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadAllChatMsgesByOtherUserId(LoadAllChatMsgesByOtherUserIdPostParams postParams, final OnLoadAllChatMsgesByOtherUserIdResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_CHATMSGES;
	RequestParams params = new RequestParams();
	params.put("other_user_id", postParams.otherUserId);
	params.put("page", postParams.page);
	params.put("page_size", postParams.pageSize);
	params.put("last_msg_time", postParams.lastMsgTime);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("聊天消息:" + res);

		List<ChatMessage> msges = new ArrayList<ChatMessage>();

		try {
		    JSONArray jsonArr = new JSONArray(res);
		    int length = jsonArr.length();
		    for (int i = 0, n = length; i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			ChatMessage msg = new ChatMessage();
			msges.add(msg);

			int id = jsonObj.getInt("id");

			String fromUserId = jsonObj.getString("from_user_id");
			String toUserId = jsonObj.getString("to_user_id");

			String face = jsonObj.getString("face");
			String content = jsonObj.getString("content");
			
			int time = jsonObj.getInt("time"); // 消息时间

			String imageUrl = null;
			if (!jsonObj.isNull("image_url")) {
			    imageUrl = jsonObj.getString("image_url");
			}
			String voiceUrl = jsonObj.getString("voice_url");
			msg.setId(id);

			if (face.startsWith(".")) {
			    face = Constants.HOST + face.substring(1);
			}
			msg.setAvatarUrl(face);
			msg.setFromUserId(fromUserId);
			msg.setTargetUserId(toUserId);
			msg.setContent(content);
			msg.setImageUrl(imageUrl);
			msg.setVoiceUrl(voiceUrl);
			
			msg.setTime(time);

			msg.setMsgStatus(ChatMessage.HISTORY_LOADED);

		    }

		    listener.onSuccess(msges);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void deleteMessagesByTime(DeleteChatMessagesPostParams postParams, final OnDeleteChatMessagesResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_DELETE_CHATMSGES;
	RequestParams params = new RequestParams();
	params.put("other_user_id", postParams.otherUserId);
	params.put("last_msg_time", postParams.lastMsgTime);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		listener.onSuccess();
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }
	};

	HttpUtil.post(url, params, responseHandler);
    }

}
