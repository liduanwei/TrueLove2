package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceChatImpl;
import me.himi.love.IAppServiceChat.DeleteChatMessagesPostParams;
import me.himi.love.IAppServiceChat.LoadAllChatMsgesByOtherUserIdPostParams;
import me.himi.love.IAppServiceChat.OnDeleteChatMessagesResponseListener;
import me.himi.love.IAppServiceChat.OnLoadAllChatMsgesByOtherUserIdResponseListener;
import me.himi.love.IAppServiceChat.OnSendMessageResponseListener;
import me.himi.love.IAppServiceChat.SendMessagePostParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.ChatMessagesAdapter;
import me.himi.love.dao.ChatMsgDBHelper;
import me.himi.love.entity.ChatMessage;
import me.himi.love.entity.FriendUser;
import me.himi.love.im.adapter.EmoViewPagerAdapter;
import me.himi.love.im.adapter.EmoteAdapter;
import me.himi.love.im.entity.FaceText;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ChatActivity extends BaseActivity implements OnClickListener {

    FriendUser targetUser;
    XListView mListView;

    ChatMessagesAdapter mAdapter;

    TextView tvPublish;
    EditText etContent;

    TextView tvShowEmo;

    TextView tvAddImage;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_chat);

	init();

    }

    private void init() {

	Object o = getIntent().getSerializableExtra("target_chat_user");
	if (null != o) {
	    FriendUser user = (FriendUser) o;
	    this.targetUser = user;
	} else {
	    showToast("未指定用户");
	    finish();
	}

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("更多");

	tvTopTitle.setText(targetUser.getNickname());

	mListView = (XListView) findViewById(R.id.listview);
	mListView.setPullRefreshEnable(false);
	mListView.setPullLoadEnable(true);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadMessagesFromServer();
	    }
	});

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	mAdapter = new ChatMessagesAdapter(this, new ArrayList<ChatMessage>());

	mListView.setAdapter(mAdapter);

	// 发布
	tvPublish = (TextView) findViewById(R.id.tv_chat_message_publish);
	tvPublish.setOnClickListener(this);

	// 
	etContent = (EditText) findViewById(R.id.et_chat_message_content);
	etContent.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		View view = findViewById(R.id.layout_emo);
		view.setVisibility(View.GONE);
		ActivityUtil.showSoftInputView(ChatActivity.this, etContent);
	    }
	});

	tvShowEmo = (TextView) findViewById(R.id.tv_chat_message_emo);
	tvShowEmo.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 显示或隐藏表情视图
		View view = findViewById(R.id.layout_emo);
		if (view.getVisibility() == View.VISIBLE) {
		    view.setVisibility(View.GONE);
		    //		    ActivityUtil.showSoftInputView(UserNewsCommentActivity.this, etCommentContent);

		} else {
		    view.setVisibility(View.VISIBLE);
		    ActivityUtil.closeSoftInput(ChatActivity.this, etContent);
		}
	    }
	});

	// 添加图片
	tvAddImage = (TextView) findViewById(R.id.tv_chat_message_addimage);
	tvAddImage.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 显示或隐藏视图
		View view = findViewById(R.id.layout_insert_image);
		if (view.getVisibility() == View.VISIBLE) {
		    view.setVisibility(View.GONE);
		    //		    ActivityUtil.showSoftInputView(UserNewsCommentActivity.this, etCommentContent);

		} else {
		    view.setVisibility(View.VISIBLE);
		    ActivityUtil.closeSoftInput(ChatActivity.this, etContent);
		}
	    }
	});

	// 默认先隐藏软键盘
	ActivityUtil.hideSoftInput(this);
	// 初始显示加载...

	// 加载本地记录
	loadMessagesFromLocal();

	// 加载服务器上的消息
	loadMessagesFromServer();

	// 初始化表情view
	initEmoView();
    }

    int pageNumber = 1;

    int lastMsgTime = 0; //(本地接收到的)最后消息时间

    private void loadMessagesFromServer() {

	LoadAllChatMsgesByOtherUserIdPostParams postParams = new LoadAllChatMsgesByOtherUserIdPostParams();
	postParams.otherUserId = targetUser.getUserId();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	postParams.lastMsgTime = lastMsgTime; // 上次接收到该用户的消息的时间

	AppServiceChatImpl.getInstance().loadAllChatMsgesByOtherUserId(postParams, new OnLoadAllChatMsgesByOtherUserIdResponseListener() {

	    @Override
	    public void onSuccess(List<ChatMessage> msges) {
		// TODO Auto-generated method stub
		if (msges.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(msges);
		    saveToDB(msges);
		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无数据");
		}

		mListView.stopLoadMore();
		mListView.stopRefresh();

		pageNumber++;
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);

		mListView.stopLoadMore();
		mListView.stopRefresh();
	    }
	});

	// 测试
	//	ChatMessage msg = null;
	//	msg = new ChatMessage();
	//	msg.setAvatarUrl("http://t11.baidu.com/it/u=681147538,4150610907&fm=58");
	//	msg.setFromUserId(1 + "");
	//	msg.setMsgStatus(ChatMessage.SEND_SUCCESSED);
	//	msg.setContent("嗨,最近还好吗?");
	//	msg.setTimeStr("5分钟前");
	//	mAdapter.add(msg);
	//

    }

    /**
     * 保存到本地数据库
     */
    private void saveToDB(List<ChatMessage> msges) {
	ChatMsgDBHelper dbHelper = ChatMsgDBHelper.getInstance(this);

	ChatMessage lastMsg = null;
	
	for (ChatMessage msg : msges) {
	    dbHelper.insertMessage(msg);
	    
	    lastMsg = msg;
	}
	
	if(null != lastMsg) {
	    DeleteChatMessagesPostParams postParams = new DeleteChatMessagesPostParams();
	    postParams.otherUserId = targetUser.getUserId();
	    postParams.lastMsgTime = lastMsg.getTime();
	    
	    AppServiceChatImpl.getInstance().deleteMessagesByTime(postParams, new OnDeleteChatMessagesResponseListener() {
	        
	        @Override
	        public void onSuccess() {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public void onFailure(String errorMsg) {
	    	// TODO Auto-generated method stub
	    	
	        }
	    });
	}
    }

    /**
     * 加载本地历史记录消息
     */
    private void loadMessagesFromLocal() {
	ChatMsgDBHelper dbHelper = ChatMsgDBHelper.getInstance(this);
	String userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "";
	String otherUserId = targetUser.getUserId() + "";
	List<ChatMessage> msges = dbHelper.selectChatMessagesByUserId(userId, otherUserId);

	if (msges.size() != 0) {
	    this.mAdapter.addAll(msges);
	    
	    lastMsgTime = msges.get(0).getTime();
	}
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	if (v == tvPublish) { /// 发送
	    sendMessage();

	}
    }

    private void sendMessage() {
	String content = etContent.getText().toString().trim();
	if (content.length() == 0) {
	    showToast("请输入内容");
	    return;
	}
	etContent.setText(""); // 清空输入框

	final ChatMessage msg = new ChatMessage();
	msg.setMsgStatus(ChatMessage.SENDING);
	msg.setFromUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
	msg.setContent(content);
	msg.setImageUrl(null);
	msg.setVoiceUrl("");

	mAdapter.add(msg);
	// 滚动到底部
	mListView.setSelection(mAdapter.getCount() - 1);

	SendMessagePostParams postParams = new SendMessagePostParams();
	postParams.fromUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "";
	postParams.toUserId = targetUser.getUserId() + "";
	postParams.content = content;
	postParams.imageUrl = "";
	postParams.voiceUrl = "";

	AppServiceChatImpl.getInstance().sendMessage(postParams, new OnSendMessageResponseListener() {

	    @Override
	    public void onSuccess(ChatMessage chatMessage) {
		// TODO Auto-generated method stub
		msg.setContent(chatMessage.getContent());
		msg.setMsgStatus(ChatMessage.SEND_SUCCESSED);
		msg.setAvatarUrl(chatMessage.getAvatarUrl());
		mAdapter.notifyDataSetChanged();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
		msg.setMsgStatus(ChatMessage.SEND_FAILED);
		mAdapter.notifyDataSetChanged();
	    }
	});
    }

    private void initEmoView() {
	ViewPager viewpager = (ViewPager) findViewById(R.id.pager_emo);
	List<View> views = new ArrayList<View>();
	List<FaceText> emos01 = FaceTextUtils.faceTexts;
	views.add(getGridView(emos01));
//	List<FaceText> emos02 = FaceTextUtils.faceTexts2;
//	views.add(getGridView(emos02));
	viewpager.setAdapter(new EmoViewPagerAdapter(views));

    }

    private View getGridView(List<FaceText> emos) {
	View v = LayoutInflater.from(this).inflate(R.layout.emo_gridview, null);
	GridView gridview = (GridView) v.findViewById(R.id.gv_emo);

	List<FaceText> list = new ArrayList<FaceText>();
	list.addAll(emos);
	final EmoteAdapter gridAdapter = new EmoteAdapter(this, list);
	gridview.setAdapter(gridAdapter);
	gridview.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		FaceText name = (FaceText) gridAdapter.getItem(position);
		String key = name.text.toString();
		try {
		    if (etContent != null && !TextUtils.isEmpty(key)) {
			int start = etContent.getSelectionStart();
			CharSequence content = etContent.getText().insert(start, key);
			etContent.setText(content);
			// 定位光标位置
			CharSequence info = etContent.getText();
			if (info instanceof Spannable) {
			    Spannable spanText = (Spannable) info;
			    Selection.setSelection(spanText, start + key.length());
			}
		    }
		} catch (Exception e) {

		}
	    }
	});
	return gridview;
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	View view = findViewById(R.id.layout_emo); // 表情布局可见时按返回键隐藏
	if (view.getVisibility() == View.VISIBLE) {
	    view.setVisibility(View.GONE);
	    return;
	}
	super.onBackPressed();
    }

}
