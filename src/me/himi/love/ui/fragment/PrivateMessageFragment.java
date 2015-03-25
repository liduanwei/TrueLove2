package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.PrivateMessageAdapter;
import me.himi.love.boost.androidservice.MessagePollService;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.PrivateMessage;
import me.himi.love.entity.PrivateMessage.MessageType;
import me.himi.love.ui.MyReceivedChatMessagesActivity;
import me.himi.love.ui.MyReceivedFansActivity;
import me.himi.love.ui.MyReceivedQuestionsActivity;
import me.himi.love.ui.MyReceivedSayhiActivity;
import me.himi.love.ui.MyReceivedSystemMessageActivity;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.UserVisitorsActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.ImageLoaderOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class PrivateMessageFragment extends BaseFragment implements OnItemClickListener {

    me.himi.love.view.list.XListView mListView;

    PrivateMessageAdapter mMsgAdapater;

    View mContainerView;

    TextView tvMessageTips; // 新消息提示

    // 私信 广播接收器
    private BroadcastReceiver privateMessageReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    String action = intent.getAction();

	    if (MessagePollService.ACTION_PRIVATE_MESSAGE.equals(action)) {
		/// 根据消息的类型来处理
		//		PrivateMessage msg = (PrivateMessage) intent.getSerializableExtra("message");

		// 
		// 有新消息
		queryPrivateMessages();
		//		DBHelper dbHelper = DBHelper.getInstance(getActivity(), DBHelper.DB_NAME, null, 1);
		//
		//		List<PrivateMessage> msges = dbHelper.selectUnreadMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
		//
		//		for (PrivateMessage msg : msges) {
		//		    addMessage(msg);
		//		}

		//				getActivity().findViewById(R.id.tv_message_tips).setVisibility(isHidden() ? View.VISIBLE : View.GONE);
		//		tvMessageTips.setVisibility(hiddenMessageCount == 0 ? View.GONE : View.VISIBLE);
		//tvMessageTips.setText(hiddenMessageCount + "");
	    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 屏幕关闭后 降低刷新频率(增大间隔时间)
	    }
	}

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    };

    /**
     * 会被多次调用
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	mContainerView = inflater.inflate(R.layout.fragment_message_private, container, false);
	init();
	initReceiver();
	return mContainerView;
    }

    /**
     *会被多次调用
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	//	init();
	//	initReceiver();
    }

    private void initReceiver() {

	// TODO Auto-generated method stub
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(MessagePollService.ACTION_PRIVATE_MESSAGE);
	intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	//	intentFilter.setPriority(2147483647); // 设为最高优先级
	getActivity().registerReceiver(privateMessageReceiver, intentFilter);
    }

    // 6 类私信
    PrivateMessage sayhiCountMsg;
    PrivateMessage fansCountMsg;
    PrivateMessage questionsCountMsg;
    PrivateMessage visitorCountMsg;
    PrivateMessage systemCountMsg;
    //    PrivateMessage chatCountMsg;
    // 放在外部确保只初始化一次
    List<PrivateMessage> messages = new ArrayList<PrivateMessage>() {
	{
	    // 供查看基本消息入口(点击进入新页面查看)
	    //	    sayhiCountMsg = new SayHiPrivateMessage();
	    //	    sayhiCountMsg.setType(MessageType.SAYHI);
	    //	    sayhiCountMsg.setTitle("打招呼");
	    //	    sayhiCountMsg.setContent("暂无新招呼");
	    //	    sayhiCountMsg.setIcon("");
	    //	    add(sayhiCountMsg);
	    //
	    //	    fansCountMsg = new FansPrivateMessage();
	    //	    fansCountMsg.setType(MessageType.FOLLOW);
	    //	    fansCountMsg.setTitle("粉丝");
	    //	    fansCountMsg.setContent("暂无新粉丝");
	    //	    fansCountMsg.setIcon("");
	    //	    add(fansCountMsg);
	    //
	    //	    questionsCountMsg = new QuestionPrivateMessage();
	    //	    questionsCountMsg.setType(MessageType.QUESTION);
	    //	    questionsCountMsg.setTitle("提问");
	    //	    questionsCountMsg.setContent("暂无新提问");
	    //	    questionsCountMsg.setIcon("");
	    //	    add(questionsCountMsg);

	}
    };

    private void init() {

	// 消息提示 数字
	tvMessageTips = (TextView) getActivity().findViewById(R.id.tv_message_tips);
	tvMessageTips.setVisibility(View.GONE);

	mListView = (me.himi.love.view.list.XListView) mContainerView.findViewById(R.id.listview_message_private);
	// 禁用下拉刷新和加载更多
	mListView.setPullLoadEnable(false);
	mListView.setPullRefreshEnable(false);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	mMsgAdapater = new PrivateMessageAdapter(this.getActivity(), messages);

	mListView.setAdapter(mMsgAdapater);

	mMsgAdapater.notifyDataSetChanged();

	//initBaseMessages(); // 供查看基本消息入口(点击进入新页面查看)

	// 初始化弹窗
	initPopupView();

	queryPrivateMessages(); // 首次启动即查询消息请求

	mListView.setOnItemClickListener(this);
    }

    View popupView;
    TextView tvSayhiTitle, tvSayhiContent, tvSayhiClose;
    ImageView ivSayhiUserFace;

    private void initPopupView() {
	// 弹窗 view
	popupView = getActivity().findViewById(R.id.popup_sayhi);
	ivSayhiUserFace = (ImageView) popupView.findViewById(R.id.iv_user_face);
	tvSayhiTitle = (TextView) popupView.findViewById(R.id.tv_sayhi_title);
	tvSayhiContent = (TextView) popupView.findViewById(R.id.tv_sayhi_content);
	tvSayhiClose = (TextView) popupView.findViewById(R.id.tv_sayhi_close);
	tvSayhiClose.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		hidePopupSayhi();
	    }
	});
    }

    /**
     * 展示弹窗
     */
    private void showPopupSayhi(final String icon, String content, final String userId, final String nickname) {
	// 显示弹窗
	tvSayhiContent.setText(content);
	tvSayhiTitle.setText(nickname);
	ImageLoader.getInstance().displayImage(icon, ivSayhiUserFace, ImageLoaderOptions.normalOptions());

	this.popupView.setVisibility(View.VISIBLE);
	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 200, 0);
	transAnimation.setDuration(300);
	animationSet.addAnimation(transAnimation);
	popupView.startAnimation(animationSet);

	this.popupView.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 隐藏弹窗
		hidePopupSayhi(); //

		// 查看该用户的资料
		Intent intent = new Intent();
		intent.setClass(getActivity(), UserInfoTextActivity.class);
		intent.putExtra("user_id", Integer.parseInt(userId)); // ID
		intent.putExtra("user_nickname", nickname); // 昵称
		intent.putExtra("user_face_url", icon); //用户头像

		getActivity().startActivity(intent); // 启动 activity
	    }
	});

    }

    /**
     * 隐藏弹窗
     */
    private void hidePopupSayhi() {
	this.popupView.setVisibility(View.GONE);
	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 0, 200);
	transAnimation.setDuration(500);
	AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.001f);
	alphaAnimation.setDuration(500);

	animationSet.addAnimation(transAnimation);
	animationSet.addAnimation(alphaAnimation);

	popupView.startAnimation(animationSet);
    }

    /**
     * 查询新消息
     */
    private void queryPrivateMessages() {
	// TODO Auto-generated method stub
	//	LoadUnreadMessagesCountParams postPar.ams = new LoadUnreadMessagesCountParams();
	//
	//	postParams.currentUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	//
	//	AppServiceExtendImpl.getInstance().loadUnreadMessagesCount(postParams, new OnLoadUnreadMessagesCountResponseListener() {
	//
	//	    @Override
	//	    public void onSuccess(UnreadMessagesCountResponse countResponse) {
	//		if (countResponse.hiCount != 0) {
	//		    sayhiCountMsg.setContent("你有" + countResponse.hiCount + "条新招呼");
	//		    mMsgAdapater.notifyDataSetChanged();
	//		}
	//		if (countResponse.fansCount != 0) {
	//
	//		    fansCountMsg.setContent("你有" + countResponse.fansCount + "个新粉丝");
	//		    mMsgAdapater.notifyDataSetChanged();
	//		}
	//		if (countResponse.questionsCount != 0) {
	//		    questionsCountMsg.setContent("你有" + countResponse.questionsCount + "个新问题");
	//		    mMsgAdapater.notifyDataSetChanged();
	//		}
	//	    }
	//
	//	    @Override
	//	    public void onFailure(String errorMsg) {
	//	    }
	//	});

	// 查询本地数据库
	DBHelper dbHelper = DBHelper.getInstance(getActivity(), DBHelper.DB_NAME, null, 1);
	List<PrivateMessage> msges = dbHelper.selectAllMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	//	List<PrivateMessage> msges = dbHelper.selectUnreadMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());

	for (PrivateMessage msg : msges) {
	    addMessage(msg);
	}

    }

    Handler handler = new Handler();

    /**
     */
    @Override
    public void onResume() {
	super.onResume();
    }

    //    private int hiddenMessageCount = 0;

    public void addMessage(PrivateMessage msg) {
	if (msg.getType() == MessageType.SAYHI) {
	    if (sayhiCountMsg == null) {
		sayhiCountMsg = new PrivateMessage();
		sayhiCountMsg.setType(MessageType.SAYHI);
		mMsgAdapater.add(sayhiCountMsg);
	    }

	    sayhiCountMsg.setTitle("新朋友打招呼"); // 存入数据库后因title内容不符||则不再弹窗
	    sayhiCountMsg.setCount(msg.getCount()); // 显示未读数量
	    sayhiCountMsg.setContent(msg.getCount() != 0 ? msg.getContent() : "[ 暂无新招呼 ]");
	    sayhiCountMsg.setTime(msg.getTime());
	    sayhiCountMsg.setIcon(msg.getIcon());
	    mMsgAdapater.notifyDataSetChanged();

	    // 展示 打招呼 弹窗
	    if (msg.getTitle() == null || !msg.getTitle().contains("=_=")) {
		//		showToast("null" + msg.getTitle());
		System.out.println("hi title:" + msg.getTitle());
	    } else {
		String[] nameAndId = msg.getTitle().split("=_=");
		showPopupSayhi(msg.getIcon(), msg.getContent(), nameAndId[1], nameAndId[0]);
	    }

	} else if (msg.getType() == MessageType.FOLLOW) {
	    if (fansCountMsg == null) {
		fansCountMsg = new PrivateMessage();
		fansCountMsg.setType(MessageType.FOLLOW);
		mMsgAdapater.add(fansCountMsg);
	    }
	    fansCountMsg.setTitle(msg.getTitle());
	    fansCountMsg.setCount(msg.getCount()); // 显示未读数量
	    fansCountMsg.setContent(msg.getCount() != 0 ? "你有新粉丝" : "暂无新粉丝");
	    fansCountMsg.setTime(msg.getTime());
	    fansCountMsg.setIcon(msg.getIcon());
	    mMsgAdapater.notifyDataSetChanged();
	} else if (msg.getType() == MessageType.QUESTION) {
	    if (questionsCountMsg == null) {
		questionsCountMsg = new PrivateMessage();
		questionsCountMsg.setType(MessageType.QUESTION);
		mMsgAdapater.add(questionsCountMsg);
	    }
	    questionsCountMsg.setTitle(msg.getTitle());
	    questionsCountMsg.setCount(msg.getCount()); // 显示未读数量
	    questionsCountMsg.setContent(msg.getCount() != 0 ? "你有未读新问题" : "暂无新提问");
	    questionsCountMsg.setTime(msg.getTime());
	    questionsCountMsg.setIcon(msg.getIcon());
	    mMsgAdapater.notifyDataSetChanged();
	} else if (msg.getType() == MessageType.VISITOR) {
	    if (visitorCountMsg == null) {
		visitorCountMsg = new PrivateMessage();
		visitorCountMsg.setType(MessageType.VISITOR);
		mMsgAdapater.add(visitorCountMsg);
	    }
	    visitorCountMsg.setTitle(msg.getTitle());
	    visitorCountMsg.setCount(msg.getCount()); // 显示未读数量
	    visitorCountMsg.setContent(msg.getCount() != 0 ? msg.getContent() : "暂无新访客");
	    visitorCountMsg.setTime(msg.getTime());
	    visitorCountMsg.setIcon(msg.getIcon());
	    mMsgAdapater.notifyDataSetChanged();
	} else if (msg.getType() == MessageType.SYSTEM) {
	    if (systemCountMsg == null) {
		systemCountMsg = new PrivateMessage();
		systemCountMsg.setType(MessageType.SYSTEM);
		mMsgAdapater.add(systemCountMsg);
	    }
	    systemCountMsg.setTitle(msg.getTitle());
	    systemCountMsg.setCount(msg.getCount()); // 显示未读数量
	    systemCountMsg.setContent(msg.getCount() != 0 ? msg.getContent() : "暂无新消息");
	    systemCountMsg.setTime(msg.getTime());
	    systemCountMsg.setIcon(msg.getIcon());
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.CHAT) {
	    //	    if (chatCountMsg == null) {
	    //		chatCountMsg = new PrivateMessage();
	    //		chatCountMsg.setType(MessageType.CHAT);
	    //		mMsgAdapater.add(chatCountMsg);
	    //	    }
	    //	    chatCountMsg.setTitle(msg.getTitle());
	    //	    chatCountMsg.setCount(msg.getCount()); // 显示未读数量
	    //	    chatCountMsg.setContent(msg.getCount() != 0 ? msg.getContent() : "暂无新消息");
	    //	    chatCountMsg.setTime(msg.getTime());
	    //	    chatCountMsg.setIcon(msg.getIcon());
	    //	    mMsgAdapater.notifyDataSetChanged();
	} else {
	    mMsgAdapater.add(msg);
	}
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
	// 这里无效,只有父MessageFragment才会有该事件
	if (!hidden) {
	    tvMessageTips.setVisibility(View.GONE);
	}
	super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
	// TODO Auto-generated method stub
	// 解除消息广播接收
	getActivity().unregisterReceiver(privateMessageReceiver);
	super.onDestroy();
    }

    @Override
    public void onDestroyView() {
	//	getActivity().unregisterReceiver(privateMessageReceiver);
	super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	position -= 1;
	PrivateMessage msg = mMsgAdapater.getList().get(position);
	DBHelper dbHelper = DBHelper.getInstance(getActivity(), DBHelper.DB_NAME, null, 1);
	if (msg.getType() == MessageType.SAYHI) {
	    Intent intent = new Intent(getActivity(), MyReceivedSayhiActivity.class);
	    getActivity().startActivity(intent);
	    // 更新打招呼消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.SAYHI.ordinal());
	    //	    mMsgAdapater.getList().clear();

	    sayhiCountMsg.setCount(0);
	    sayhiCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.FOLLOW) {
	    Intent intent = new Intent(getActivity(), MyReceivedFansActivity.class);
	    getActivity().startActivity(intent);
	    // 更新粉丝消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.FOLLOW.ordinal());

	    fansCountMsg.setCount(0);
	    fansCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.QUESTION) {
	    Intent intent = new Intent(getActivity(), MyReceivedQuestionsActivity.class);
	    getActivity().startActivity(intent);
	    // 更新提问消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.QUESTION.ordinal());
	    //	    mMsgAdapater.getList().clear();
	    questionsCountMsg.setCount(0);
	    questionsCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();
	} else if (msg.getType() == MessageType.VISITOR) {
	    Intent intent = new Intent(getActivity(), UserVisitorsActivity.class);
	    intent.putExtra("target_user_id", MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	    intent.putExtra("user_nickname", MyApplication.getInstance().getCurrentLoginedUser().getNickname());
	    startActivity(intent);

	    // 更新访客消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.VISITOR.ordinal());

	    visitorCountMsg.setCount(0);
	    visitorCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.SYSTEM) {
	    showToast("困猫小游戏");
	    Intent intent = new Intent(getActivity(), MyReceivedSystemMessageActivity.class);
	    startActivity(intent);
	    // 更新系统消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.SYSTEM.ordinal());

	    queryPrivateMessages();

	} else if (msg.getType() == MessageType.CHAT) {
	    Intent intent = new Intent(getActivity(), MyReceivedChatMessagesActivity.class);
	    startActivity(intent);
	    // 更新聊天消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.CHAT.ordinal());
	    queryPrivateMessages();
	}
    }
}
