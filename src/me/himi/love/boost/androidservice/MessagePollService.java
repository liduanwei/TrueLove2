package me.himi.love.boost.androidservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import me.himi.love.MyApplication;
import me.himi.love.dao.DBHelper;
import me.himi.love.dao.DBHelper.RecommendUser;
import me.himi.love.entity.PrivateMessage;
import me.himi.love.entity.PrivateMessage.MessageType;
import me.himi.love.entity.loader.impl.UserDetailLoaderImpl;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * 轮询获取各类消息,  系统自动推荐的配对好友(作为私信)
 * @ClassName:PrivateMessagePollService
 * @author sparklee liduanwei_911@163.com
 * @date Nov 9, 2014 3:55:08 PM
 */
public class MessagePollService extends Service {

    //    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    private static Timer requestTimer;

    // 广播 私信消息action
    public static final String ACTION_PRIVATE_MESSAGE = "me.himi.love.private_msg";
    // 广播 系统消息action
    public static final String ACTION_SYSTEM_MESSAGE = "me.himi.love.private_msg";
    // 更多其他消息
    // ...

    public static final long MAX_DELAY_TIME_MILLIS = 1000 * 60 * 20; // 轮询最大间隔时间
    public static final long MIN_DELAY_TIME_MILLIS = 1000 * 9; // 轮询最小间隔时间

    private long currentDelayTime = MIN_DELAY_TIME_MILLIS; // 当前间隔时间

    private IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return binder;
    }

    public class MyBinder extends Binder {
	public MessagePollService getService() {
	    return MessagePollService.this;
	}
    }

    private boolean isRunning = true;

    @Override
    public void onCreate() {
	super.onCreate();
	requestTimer = new Timer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

	TimerTask task = new TimerTask() {

	    @Override
	    public void run() {
		if (isRunning) {
		    // 获取各种私信 消息的数量
		    loadMessagesFromServer();
		}
	    }
	};

	requestTimer.schedule(task, 1000, currentDelayTime);

	return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取私信的各种消息的数量
     */
    private void loadMessagesFromServer() {
	// TODO Auto-generated method stub
	loadHi();
	loadFans();
	loadQuestion();
	loadVisitor();
	loadSystem();

	// 获取好友的最新一条聊天消息
	//loadChatMessages();
    }

    /**
     * 
     */
    private void loadChatMessages() {

	String url = Constants.URL_USER_MESSAGE_CHAT;
	RequestParams params = new RequestParams();
	params.add("user_id", ".");
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("chat msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);
			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);

			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.CHAT);
			msg.setTitle(title);
			msg.setContent(content);
			msg.setCount(count);

			msg.setLastMsgTime(lastMsgTime);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			msg.setIcon(icon);

			//通知服务器记录最后收到的消息的时间
			notifyChatMessageReceived(lastMsgTime);

			intent.putExtra("message_type", MessageType.CHAT.ordinal()); // 消息类型

			// 保存消息到本地
			DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);
			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			db.insertMessage(msg);
			//			db.close();
			System.out.println("聊天消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);

		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}

	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});

    }

    private void loadSystem() {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_MESSAGE_SYSTEM;
	RequestParams params = new RequestParams();
	params.add("user_id", ".");
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("sys msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);
			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);

			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.SYSTEM);
			msg.setTitle(title);
			msg.setContent(content);
			msg.setCount(count);

			msg.setLastMsgTime(lastMsgTime);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			msg.setIcon(icon);

			intent.putExtra("message_type", MessageType.SYSTEM.ordinal()); // 消息类型

			//通知服务器记录最后收到的消息的时间
			notifySystemMessageReceived(lastMsgTime);

			// 保存消息到本地
			DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);
			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			db.insertMessage(msg);
			//			db.close();
			System.out.println("系统消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);

		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}

	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});
    }

    private void loadVisitor() {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_MESSAGE_VISITOR;
	RequestParams params = new RequestParams();
	params.add("user_id", "xxxxxxxx");
	final DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);

	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("visitor msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);

			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);
			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.VISITOR);
			msg.setTitle("谁看过我");
			msg.setContent("有" + content + "个人看过你的资料");
			msg.setLastMsgTime(lastMsgTime);
			msg.setCount(count);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			msg.setIcon(icon);

			// 访客消息
			intent.putExtra("message_type", MessageType.VISITOR.ordinal()); // 消息类型

			// 发送已接收成功的消息时间,服务器端会进行保存记录
			notifyVisitorMessageReceived(lastMsgTime);

			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			// 保存消息到本地
			db.insertMessage(msg);
			//			db.close();

			System.out.println("访客消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}
	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});
    }

    private void loadQuestion() {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_MESSAGE_QUESTION;
	RequestParams params = new RequestParams();
	params.add("user_id", "xxxxxxxx");
	final DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);

	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("question msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);

			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);
			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.QUESTION);
			msg.setTitle("新朋友提问");
			msg.setContent("你有新提问");
			msg.setLastMsgTime(lastMsgTime);
			msg.setCount(count);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			msg.setIcon(icon);

			intent.putExtra("message_type", MessageType.QUESTION.ordinal()); // 消息类型
			
			
			// 发送已接收成功的消息时间,服务器端会进行保存记录
			notifyQuestionMessageReceived(lastMsgTime);

			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			// 保存消息到本地
			db.insertMessage(msg);
			//			db.close();

			System.out.println("问题消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}
	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});
    }

    private void loadFans() {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_MESSAGE_FANS;
	RequestParams params = new RequestParams();
	params.add("user_id", "xxxxxxxx");
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("fans msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);
			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);
			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.FOLLOW);
			msg.setTitle("新粉丝");
			msg.setContent("你有新粉丝");
			msg.setCount(count);

			msg.setLastMsgTime(lastMsgTime);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			msg.setIcon(icon);

			intent.putExtra("message_type", MessageType.FOLLOW.ordinal()); // 消息类型
			
			
			//通知服务器记录最后收到的消息的时间
			notifyFansMessageReceived(lastMsgTime);

			// 保存消息到本地
			DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);
			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			db.insertMessage(msg);
			//			db.close();
			System.out.println("粉丝消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);

		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}

	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});
    }

    private void loadHi() {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_MESSAGE_HI;
	RequestParams params = new RequestParams();
	params.add("user_id", "xxxxxxxx");
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
		// TODO Auto-generated method stub
		//		System.out.println("PrivateMessagePollService 预处理Response:" + arg1.toString());
	    }

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		    System.out.println("hi msg:" + data);

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}

		if (null != data) {
		    try {
			JSONObject jsonObj = new JSONObject(data);
			String title = jsonObj.getString("title");
			String content = jsonObj.getString("content");
			String icon = jsonObj.getString("icon");
			int lastMsgTime = jsonObj.getInt("last_time");
			int time = jsonObj.getInt("time");

			int count = jsonObj.getInt("count");

			Intent intent = new Intent();
			intent.setAction(ACTION_PRIVATE_MESSAGE);
			PrivateMessage msg = new PrivateMessage();
			msg.setType(MessageType.SAYHI);
			msg.setTitle(title);
			msg.setContent(content);
			msg.setCount(count);
			msg.setLastMsgTime(lastMsgTime);

			msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
			if (icon.startsWith(".")) {
			    icon = Constants.HOST + icon.substring(1);
			}
			msg.setIcon(icon);
			
			// hi
			intent.putExtra("message_type", MessageType.SAYHI.ordinal()); // 消息类型
			

			//通知服务器记录最后收到的消息的时间
			notifyHiMessageReceived(lastMsgTime);

			// 保存消息到本地
			DBHelper db = DBHelper.getInstance(getApplicationContext(), DBHelper.DB_NAME, null, 1);
			msg.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
			db.insertMessage(msg);
			//			db.close();

			System.out.println("打招呼消息插入成功");

			// 广播通知消息
			sendBroadcast(intent);

		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		    }
		}

	    }

	    @Override
	    public Header[] getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	    }
	});
    }

    /**
     * 发送已收到最新消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifyQuestionMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_QUESTION_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {
	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub
		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后问题消息时间:" + data);
	    }
	});
    }

    /**
     * 发送已收到最新消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifyHiMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_HI_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {
	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub
		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后打招呼消息时间:" + data);
	    }
	});
    }

    /**
     * 发送已收到最新消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifyFansMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_FANS_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后粉丝消息时间:" + data);
		super.onPostProcessResponse(arg0, response);
	    }
	});
    }

    /**
     * 发送已收到最新消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifyVisitorMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_VISITOR_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后访客消息时间:" + data);
		super.onPostProcessResponse(arg0, response);
	    }
	});
    }

    /**
     * 发送已收到最新消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifySystemMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_SYSTEM_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后系统消息时间:" + data);
		super.onPostProcessResponse(arg0, response);
	    }
	});
    }

    /**
     * 发送已收到最新聊天消息的时间,服务器端会进行记录
     * @param lastMsgTime
     */
    private void notifyChatMessageReceived(int lastMsgTime) {
	RequestParams params = new RequestParams();
	params.add("last_time", lastMsgTime + "");
	String url = Constants.URL_UPDATE_USER_MESSAGE_CHAT_TIME;
	HttpUtil.getClient().post(url, params, new AsyncHttpResponseHandlerAdapter() {

	    @Override
	    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse response) {
		// TODO Auto-generated method stub

		String data = null;
		try {
		    InputStream in;
		    in = response.getEntity().getContent();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;
		    while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    data = new String(baos.toByteArray());

		} catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("错误!");
		}
		System.out.println("更新最后聊天消息时间:" + data);
		super.onPostProcessResponse(arg0, response);
	    }
	});
    }

    @Override
    public void onDestroy() {
	// TODO Auto-generated method stub
	stop();
	super.onDestroy();
    }

    private void stop() {
	isRunning = false;
	stopSelf();
    }

    public void setCurrentDelayTime(long delay) {
	this.currentDelayTime = delay;
    }
}
