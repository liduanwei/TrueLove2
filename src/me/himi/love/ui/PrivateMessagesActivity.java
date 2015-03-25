package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.PrivateMessageAdapter;
import me.himi.love.boost.androidservice.MessagePollService;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.PrivateMessage;
import me.himi.love.entity.PrivateMessage.MessageType;
import me.himi.love.ui.base.BaseActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 用户收到的私信(最新访客/新粉丝/打招呼等)消息
 * @ClassName:PrivateMessagesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class PrivateMessagesActivity extends BaseActivity implements OnItemClickListener {

    TextView tvTopTitle, tvTopAction;
    TextView tvEmptyTips;
    me.himi.love.view.list.XListView mListView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_private_messages);

	init();

    }

    // 6 类私信
    PrivateMessage sayhiCountMsg;
    PrivateMessage fansCountMsg;
    PrivateMessage questionsCountMsg;
    PrivateMessage visitorCountMsg;
    PrivateMessage systemCountMsg;
    //    PrivateMessage chatCountMsg;

    PrivateMessageAdapter mMsgAdapater;

    // 放在外部确保只初始化一次
    List<PrivateMessage> messages = new ArrayList<PrivateMessage>() {
	{

	}
    };

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopTitle.setText("消息");
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

	    }
	});

	tvEmptyTips = (TextView) findViewById(R.id.tv_private_mesages_empty);

	mListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_private_msges);
	// 禁用下拉刷新和加载更多
	mListView.setPullLoadEnable(false);
	mListView.setPullRefreshEnable(false);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	mMsgAdapater = new PrivateMessageAdapter(this, messages);

	mListView.setAdapter(mMsgAdapater);

	mMsgAdapater.notifyDataSetChanged();

	initReceiver();

	queryPrivateMessages(); // 首次启动即查询消息请求

	mListView.setOnItemClickListener(this);

	// 长按条目
	mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

	    @Override
	    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		showPopup(view);
		return true;
	    }

	    TextView tvItem;

	    private void showPopup(View view) {
		// TODO Auto-generated method stub
		PopupWindow pw = new PopupWindow(PrivateMessagesActivity.this);
		LinearLayout contentView = new LinearLayout(PrivateMessagesActivity.this);
		tvItem = new TextView(PrivateMessagesActivity.this);
		tvItem.setText("删除");
		contentView.addView(tvItem);
		pw.setContentView(contentView);
		pw.setWidth(mListView.getWidth() / 2);
		pw.setHeight(100);
		pw.showAtLocation(view, Gravity.CENTER, 0, 0);
	    }
	});
    }

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

    private void initReceiver() {

	// TODO Auto-generated method stub
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(MessagePollService.ACTION_PRIVATE_MESSAGE);
	intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	//	intentFilter.setPriority(2147483647); // 设为最高优先级
	registerReceiver(privateMessageReceiver, intentFilter);
    }

    /**
     * 查询新消息
     */
    private void queryPrivateMessages() {

	// 查询本地数据库
	DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);
	List<PrivateMessage> msges = dbHelper.selectAllMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	//	List<PrivateMessage> msges = dbHelper.selectUnreadMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());

	for (PrivateMessage msg : msges) {
	    addMessage(msg);
	    System.out.println("privateMessage:" + msg.getContent());
	}

    }

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
	    //	    if (msg.getTitle() == null || !msg.getTitle().contains("=_=")) {
	    //		//		showToast("null" + msg.getTitle());
	    //		System.out.println("hi title:" + msg.getTitle());
	    //	    } else {
	    //		String[] nameAndId = msg.getTitle().split("=_=");
	    //		showPopupSayhi(msg.getIcon(), msg.getContent(), nameAndId[1], nameAndId[0]);
	    //	    }

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	position -= 1;
	PrivateMessage msg = mMsgAdapater.getList().get(position);
	DBHelper dbHelper = DBHelper.getInstance(PrivateMessagesActivity.this, DBHelper.DB_NAME, null, 1);
	if (msg.getType() == MessageType.SAYHI) {
	    Intent intent = new Intent(PrivateMessagesActivity.this, MyReceivedSayhiActivity.class);
	    startActivity(intent);
	    // 更新打招呼消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.SAYHI.ordinal());
	    //	    mMsgAdapater.getList().clear();

	    sayhiCountMsg.setCount(0);
	    sayhiCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.FOLLOW) {
	    Intent intent = new Intent(PrivateMessagesActivity.this, MyReceivedFansActivity.class);
	    startActivity(intent);
	    // 更新粉丝消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.FOLLOW.ordinal());

	    fansCountMsg.setCount(0);
	    fansCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();

	} else if (msg.getType() == MessageType.QUESTION) {
	    Intent intent = new Intent(PrivateMessagesActivity.this, MyReceivedQuestionsActivity.class);
	    startActivity(intent);
	    // 更新提问消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.QUESTION.ordinal());
	    //	    mMsgAdapater.getList().clear();
	    questionsCountMsg.setCount(0);
	    questionsCountMsg.setReaded(true);
	    mMsgAdapater.notifyDataSetChanged();
	} else if (msg.getType() == MessageType.VISITOR) {
	    Intent intent = new Intent(PrivateMessagesActivity.this, UserVisitorsActivity.class);
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
	    Intent intent = new Intent(PrivateMessagesActivity.this, MyReceivedSystemMessageActivity.class);
	    startActivity(intent);
	    // 更新系统消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.SYSTEM.ordinal());

	    queryPrivateMessages();

	} else if (msg.getType() == MessageType.CHAT) {
	    Intent intent = new Intent(PrivateMessagesActivity.this, MyReceivedChatMessagesActivity.class);
	    startActivity(intent);
	    // 更新聊天消息为已读
	    dbHelper.updateMessageToReaded(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), MessageType.CHAT.ordinal());
	    queryPrivateMessages();
	}
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	unregisterReceiver(privateMessageReceiver);
	super.onDestroy();
    }
}
