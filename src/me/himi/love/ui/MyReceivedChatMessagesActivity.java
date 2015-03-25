package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceChatImpl;
import me.himi.love.IAppServiceChat.LoadAllChatMessagesPostParams;
import me.himi.love.IAppServiceChat.OnLoadAllChatMessagesResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.MyReceivedChatMessagesAdapter;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.ReceivedChat;
import me.himi.love.entity.ReceivedFans;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 用户收到的新聊天消息
 * @ClassName:MyReceivedChatMessagesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyReceivedChatMessagesActivity extends BaseActivity implements OnItemClickListener {

    TextView tvTopTitle, tvTopAction;
    me.himi.love.view.list.XListView mListView;

    MyReceivedChatMessagesAdapter mAdapter;

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_my_chat_messages);

	init();

    }

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopTitle.setText("好友消息");

	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

	    }
	});

	mListView = (XListView) findViewById(R.id.listview);
	mListView.setPullRefreshEnable(true);
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadChatMessages();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadChatMessages();
	    }
	});

	mAdapter = new MyReceivedChatMessagesAdapter(this, new ArrayList<ReceivedChat>());

	mListView.setAdapter(mAdapter);

	mListView.setOnItemClickListener(this);

	// 所有不同用户的最新一条消息
	loadChatMessages();

    }

    private void loadChatMessages() {
	// TODO Auto-generated method stub
	LoadAllChatMessagesPostParams postParams = new LoadAllChatMessagesPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 10;
	AppServiceChatImpl.getInstance().loadAllChatMessages(postParams, new OnLoadAllChatMessagesResponseListener() {

	    @Override
	    public void onSuccess(List<ReceivedChat> msges) {
		// TODO Auto-generated method stub
		if (msges.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(msges);
		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无数据");
		}
		mListView.stopRefresh();
		mListView.stopLoadMore();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
		mListView.stopRefresh();
		mListView.stopLoadMore();
	    }
	});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	ReceivedChat chat = mAdapter.getList().get(position - 1);

	Intent intent = new Intent();
	intent.setClass(this, ChatActivity.class);
	Bundle bundle = new Bundle();

	FriendUser user = new FriendUser();
	user.setNickname(chat.getNickname());
	user.setUserId(Integer.parseInt(chat.getFromUserId()));

	bundle.putSerializable("target_chat_user", user);
	intent.putExtras(bundle);
	startActivity(intent);
    }
}
