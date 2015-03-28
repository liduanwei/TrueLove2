package me.himi.love;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.ConnectionStatusListener;
import io.rong.imkit.RongIM.GetFriendsProvider;
import io.rong.imkit.RongIM.GetUserInfoProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.himi.love.IAppServiceExtend.LoadFriendsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadFriendsResponseListener;
import me.himi.love.entity.FriendUser;
import me.himi.love.ui.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zhjchen on 1/29/15.
 */

/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 6、会话界面操作的监听器：ConversationBehaviorListener。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 */
public final class RongIMEvent implements RongIM.OnReceiveMessageListener, RongIM.GetUserInfoProvider, RongIM.GetFriendsProvider, RongIM.GetGroupInfoProvider, RongIM.ConversationBehaviorListener, RongIM.ConnectionStatusListener, RongIM.LocationProvider/*, OnSendMessageListener*/{

    private static final String TAG = RongIMEvent.class.getSimpleName();

    private static RongIMEvent mRongCloudInstance;

    private Context mContext;

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static RongIMEvent getInstance(Context context) {

	if (mRongCloudInstance == null) {

	    synchronized (RongIMEvent.class) {

		if (mRongCloudInstance == null) {
		    mRongCloudInstance = new RongIMEvent(context);
		}
	    }
	}
	return mRongCloudInstance;
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongIMEvent(Context context) {
	mContext = context;
	initDefaultListener();
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {
	//	RongIM.setGetUserInfoProvider(this, true);//设置用户信息提供者。
	//	initGetUserInfoProvider();
	//	RongIM.setGetFriendsProvider(this);//设置好友信息提供者.
	initGetFriendsProvider();

	RongIM.setGetGroupInfoProvider(this);//设置群组信息提供者。
	RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
	//	RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码

	// 连接状态改变监听
	if (RongIM.getInstance() != null) {
	    RongIM.getInstance().setConnectionStatusListener(new ConnectionStatusListener() {

		@Override
		public void onChanged(ConnectionStatus status) {
		    // TODO Auto-generated method stub
		    switch (status) {
		    case CONNECTED:
			break;
		    case DISCONNECTED:
			break;
		    case CONNECTING:
			break;
		    case KICKED_OFFLINE_BY_OTHER_CLIENT:
			break;
		    case LOGIN_ON_WEB:
			break;
		    case NETWORK_UNAVAILABLE:
			break;
		    case UNKNOWN:
			break;

		    }
		}
	    });
	}
    }

    private void initGetUserInfoProvider(final Map<String, UserInfo> users) {
	// TODO Auto-generated method stub
	RongIM.setGetUserInfoProvider(new GetUserInfoProvider() {

	    @Override
	    public UserInfo getUserInfo(String userId) {
		// TODO Auto-generated method stub
		return users.get(userId);
	    }
	}, true);
    }

    // 内存缓存好友信息
    private Map<String, UserInfo> cachedFriends = new HashMap<String, UserInfo>();

    /**
     * 加载好友
     */
    public void initGetFriendsProvider() {
	// TODO Auto-generated method stub
	LoadFriendsPostParams postParams = new LoadFriendsPostParams();
	postParams.page = 1;
	postParams.pageSize = 600;
	postParams.longtitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();

	AppServiceExtendImpl.getInstance().loadFriends(postParams, new OnLoadFriendsResponseListener() {

	    @Override
	    public void onSuccess(final List<FriendUser> friends) {
		// TODO Auto-generated method stub
		if (friends == null) {
		    return;
		}

		if (friends.size() != 0) {
		    RongIM.setGetFriendsProvider(new GetFriendsProvider() {

			@Override
			public List<UserInfo> getFriends() {
			    // TODO Auto-generated method stub
			    List<UserInfo> userInfos = new ArrayList<UserInfo>(friends.size());
			    for (FriendUser u : friends) {
				UserInfo info = new UserInfo(u.getUserId() + "", u.getNickname(), u.getFaceUrl().getSmallImageUrl());
				userInfos.add(info);

			    }
			    // 缓存好友到本地/内存
			    cacheToMemory(userInfos);
			    return userInfos;
			}
		    });

		} else {
		    //		    showToast("没有好友数据");
		}

	    }

	    private void cacheToMemory(List<UserInfo> friends) {
		// TODO Auto-generated method stub
		for (UserInfo userInfo : friends) {
		    cachedFriends.put(userInfo.getUserId(), userInfo);
		}

		// 设置 provider
		initGetUserInfoProvider(cachedFriends);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		//		showToast(errorMsg);
	    }
	});
    }

    /*
     * 连接成功注册。
     * <p/>
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {
	RongIM.getInstance().setReceiveMessageListener(this);//设置消息接收监听器。
	//	    RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
	RongIM.getInstance().setConnectionStatusListener(this);//设置连接状态监听器。    

    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    //    public static RongCloudEvent getInstance() {
    //	return mRongCloudInstance;
    //    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public void onReceived(RongIMClient.Message message, int left) {

	RongIMClient.MessageContent messageContent = message.getContent();

	String content = "你有一条新消息";
	if (messageContent instanceof TextMessage) {//文本消息
	    TextMessage textMessage = (TextMessage) messageContent;
	    content = textMessage.getContent();
	    Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
	    Log.d(TAG, "onReceived-TextMessage:" + textMessage.getPushContent());

	} else if (messageContent instanceof ImageMessage) {//图片消息
	    ImageMessage imageMessage = (ImageMessage) messageContent;
	    String s = new String(imageMessage.encode());
//	    content = s + imageMessage.getRemoteUri();
	    content = "[ 新图片消息 ]";
	    Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
	} else if (messageContent instanceof VoiceMessage) {//语音消息
	    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
	    content = "[ 新语音消息 ]";//voiceMessage.getUri().toString();
	    Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
	} else if (messageContent instanceof RichContentMessage) {//图文消息
	    RichContentMessage richContentMessage = (RichContentMessage) messageContent;
	    content = "[ 新图文消息 ]";//richContentMessage.getContent();
	    Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
	} else {
	    content = "其它消息";
	    Log.d(TAG, "onReceived-其他消息，自己来判断处理");
	}

	/**
	 * demo 代码 开发者需替换成自己的代码。
	 */
	Intent in = new Intent();
	in.setAction(MainActivity.ACTION_IM_RECEIVE_MESSAGE);

	// 启动聊天
	//	RongIM.getInstance().startPrivateChat(mContext, message.getSenderUserId(), new Stringmessage.getContent().encode());

	in.putExtra("rongCloud", RongIM.getInstance().getTotalUnreadCount());
	in.putExtra("content", content);
	mContext.sendBroadcast(in);

    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
    //    @Override
    public RongIMClient.Message onSent(RongIMClient.Message message) {

	RongIMClient.MessageContent messageContent = message.getContent();

	if (messageContent instanceof TextMessage) {//文本消息
	    TextMessage textMessage = (TextMessage) messageContent;
	    Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
	} else if (messageContent instanceof ImageMessage) {//图片消息
	    ImageMessage imageMessage = (ImageMessage) messageContent;
	    Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
	} else if (messageContent instanceof VoiceMessage) {//语音消息
	    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
	    Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
	} else if (messageContent instanceof RichContentMessage) {//图文消息
	    RichContentMessage richContentMessage = (RichContentMessage) messageContent;
	    Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
	} else {
	    Log.d(TAG, "onSent-其他消息，自己来判断处理");
	}
	return message;
    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */
    @Override
    public RongIMClient.UserInfo getUserInfo(String userId) {
	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	return null;//DemoContext.getInstance().getUserInfoById(userId);
    }

    /**
     * 好友列表的提供者：GetFriendsProvider 的回调方法，获取好友信息列表。
     *
     * @return 获取好友信息列表，（注：由开发者提供好友列表信息）。
     */
    @Override
    public List<RongIMClient.UserInfo> getFriends() {
	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	return null;
    }

    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public RongIMClient.Group getGroupInfo(String groupId) {
	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	return null;//DemoContext.getInstance().getGroupMap().get(groupId);
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onClickUserPortrait(Context context, RongIMClient.ConversationType conversationType, RongIMClient.UserInfo user) {
	Log.d(TAG, "onClickUserPortrait");

	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	//        Log.d("Begavior", conversationType.getName() + ":" + user.getName());
	//        Intent in = new Intent(context, UserInfoActivity.class);
	//        in.putExtra("user_name", user.getName());
	//        in.putExtra("user_id", user.getUserId());
	//        context.startActivity(in);

	return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onClickMessage(Context context, RongIMClient.Message message) {
	Log.d(TAG, "onClickMessage");

	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	//        if (message.getContent() instanceof LocationMessage) {
	//            Intent intent = new Intent(context, LocationActivity.class);
	//            intent.putExtra("location", message.getContent());
	//            context.startActivity(intent);
	//        }else  if(message.getContent() instanceof RichContentMessage){
	//            RichContentMessage  mRichContentMessage = (RichContentMessage) message.getContent();
	//            Log.d("Begavior",  "extra:"+mRichContentMessage.getExtra());
	//
	//        }

	Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

	return false;
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
	Log.d(TAG, "onChanged:" + status);
    }

    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context 上下文
     * @param callback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback callback) {
	/**
	 * demo 代码  开发者需替换成自己的代码。
	 */
	//	DemoContext.getInstance().setLastLocationCallback(callback);
	//	context.startActivity(new Intent(context, LocationActivity.class));//SOSO地图
    }

    public void logout() {
	mRongCloudInstance = null;

    }
}
