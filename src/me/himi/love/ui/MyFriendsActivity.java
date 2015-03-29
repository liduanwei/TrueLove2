package me.himi.love.ui;

import io.rong.imkit.RongIM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadFriendsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadFriendsResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.MyFriendsAdapter;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.Gift;
import me.himi.love.entity.UserGift;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.CacheUtils;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用户收到的打招呼
 * @ClassName:MySayhiMessagesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyFriendsActivity extends BaseActivity implements OnItemClickListener {

    private static final String TAG = "TestFragment";

    private me.himi.love.view.list.XListView mListView;

    private MyFriendsAdapter mAdapter;

    RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mContentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_myfriends, null);
	setContentView(mContentView);
	init();
    }

    private int pageNumber = 1;

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopTitle.setText("我的好友");
	tvTopTitle.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setVisibility(View.GONE);

	mListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_friends);

	mListView.setPullRefreshEnable(false);
	mListView.setPullLoadEnable(false);

	mAdapter = new MyFriendsAdapter(this, data);

	mListView.setAdapter(mAdapter);

	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position -= 1;
	    }

	});

	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadFriends();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadFriends();
	    }
	});

	//	loadFriends();
	List<FriendUser> users = CacheUtils.loadFromCache(cacheUsersPath);
	if (null == users) {
	    loadFriends();
	} else {
	    mAdapter.setList(users);
	}
	mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
	// TODO Auto-generated method stub
	//	if (data.size() == 0) {// 没有数据就刷新
	//	    pageNumber = 1;
	//	}
	//	loadFriends();

	super.onResume();
    }

    // 使用本地缓存
    private final String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/myfriends_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    private boolean isRefreshing;// 是否刷新中.

    View mLoadingView;

    private void loadFriends() {
	if (isRefreshing) {
	    return;
	}

	isRefreshing = true;

	if (mLoadingView == null) {
	    mLoadingView = getLayoutInflater().inflate(R.layout.layout_loading_retry, null);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT);
	    mContentView.addView(mLoadingView, params);

	    // 重试
	    mLoadingView.findViewById(R.id.tv_load_retry).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    loadFriends();

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);

	LoadFriendsPostParams postParams = new LoadFriendsPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	postParams.longtitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();

	AppServiceExtendImpl.getInstance().loadFriends(postParams, new OnLoadFriendsResponseListener() {

	    @Override
	    public void onSuccess(List<FriendUser> users) {
		// TODO Auto-generated method stub
		isRefreshing = false;

		if (users == null) {
		    return;
		}
		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);
		    pageNumber++;

		    // 缓存到本地
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath);
		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无好友");
		}

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}

		// 隐藏
		mLoadingView.setVisibility(View.GONE);

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub

		isRefreshing = false;

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}

		showToast(errorMsg);
		// 重试按钮隐藏
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);

	    }
	});
    }

    List<FriendUser> data = new ArrayList<FriendUser>();

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "TestFragment-----onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	FriendUser user = mAdapter.getList().get(position - 1);
	//	Intent intent = new Intent(this.getActivity(), ChatActivity.class);
	//	Bundle bundle = new Bundle();
	//	bundle.putSerializable("target_chat_user", user);
	//	intent.putExtras(bundle);
	//	startActivity(intent);

	RongIM.getInstance().startPrivateChat(this, user.getUserId() + "", user.getNickname());
	//	RongIM.getInstance().startConversation(getActivity(), ConversationType.PRIVATE, MyApplication.getInstance().getCurrentLoginedUser().getUserId()+"", "");

    }

}
