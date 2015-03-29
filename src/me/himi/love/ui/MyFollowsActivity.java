package me.himi.love.ui;

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
import me.himi.love.IAppServiceExtend.FollowParams;
import me.himi.love.IAppServiceExtend.LoadFollowParams;
import me.himi.love.IAppServiceExtend.OnFollowResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadFollowResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.MyFollowsAdapter;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.NearbyUser;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.CacheUtils;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用户收到的打招呼
 * @ClassName:MySayhiMessagesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyFollowsActivity extends BaseActivity implements OnItemClickListener {

    private static final String TAG = "TestFragment";

    private me.himi.love.view.list.XListView mListView;

    private MyFollowsAdapter mAdapter;

    RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);

	requestWindowFeature(Window.FEATURE_NO_TITLE);
	mContentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_myfollows, null);
	setContentView(mContentView);
	init();
    }

    private int pageNumber = 1;

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopTitle.setText("我关注的用户");
	tvTopTitle.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setVisibility(View.GONE);

	mListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_follows);

	mListView.setPullRefreshEnable(false);
	mListView.setPullLoadEnable(false);

	mAdapter = new MyFollowsAdapter(this, data);

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
		loadFollowUsers();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadFollowUsers();
	    }
	});

	mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		// 记录当前选择的user
		mCurrentSelectedItem = mAdapter.getList().get(info.position - 1);
		menu.add(0, 1, 0, "取消关注");

	    }
	});

	//	loadFollowUsers();
	// 从缓存加载
	List<NearbyUser> users = CacheUtils.loadFromCache(cacheUsersPath);
	if (null == users) {
	    loadFollowUsers();
	} else {
	    mAdapter.setList(users);
	}

	mListView.setOnItemClickListener(this);
    }

    // 使用本地缓存
    private final static String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/myfollows_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    @Override
    public void onResume() {
	// TODO Auto-generated method stub

	//	if (data.size() == 0) {// 没有数据就刷新
	//	    pageNumber = 1;
	//	}

	//	loadFriends();

	super.onResume();
    }

    private NearbyUser mCurrentSelectedItem;

    View mLoadingView;

    boolean isRefreshing;//

    private void loadFollowUsers() {
	if (isRefreshing)
	    return;

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
		    loadFollowUsers();

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);

	LoadFollowParams postParams = new LoadFollowParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	// 关注
	AppServiceExtendImpl.getInstance().loadFollows(postParams, new OnLoadFollowResponseListener() {

	    @Override
	    public void onSuccess(List<NearbyUser> users) {
		// TODO Auto-generated method stub
		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);

		    // 缓存到本地
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath);
		} else {

		}
		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		pageNumber++;

		// 隐藏
		mLoadingView.setVisibility(View.GONE);

		isRefreshing = false;
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		showToast(errorMsg);

		isRefreshing = false;

		// 重试按钮隐藏
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
	    }
	});
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case 1:
	    showToast("取消关注中..." + mCurrentSelectedItem.getNickname());
	    FollowParams postParams = new FollowParams();
	    postParams.followUserId = mCurrentSelectedItem.getUserId();
	    AppServiceExtendImpl.getInstance().unfollow(postParams, new OnFollowResponseListener() {

		@Override
		public void onSuccess(int status) {
		    // TODO Auto-generated method stub

		    if (-1 == status) {
			// 还未关注了该用户
			mAdapter.getList().remove(mCurrentSelectedItem);
			mAdapter.notifyDataSetChanged();
			return;
		    }
		    DBHelper db = DBHelper.getInstance(MyFollowsActivity.this, DBHelper.DB_NAME, null, 1);
		    db.deleteFollow(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), mCurrentSelectedItem.getUserId());
		    db.close();

		    mAdapter.getList().remove(mCurrentSelectedItem);
		    mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onFailure(String errorMsg) {
		    // TODO Auto-generated method stub
		    showToast(errorMsg);
		}
	    });
	    break;
	}
	return super.onContextItemSelected(item);
    }

    List<NearbyUser> data = new ArrayList<NearbyUser>();

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "TestFragment-----onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	NearbyUser user = mAdapter.getList().get(position - 1);
	//	Intent intent = new Intent(this.getActivity(), ChatActivity.class);
	//	Bundle bundle = new Bundle();
	//	bundle.putSerializable("target_chat_user", user);
	//	intent.putExtras(bundle);
	//	startActivity(intent);

	//RongIM.getInstance().startPrivateChat(this, user.getUserId() + "", user.getNickname());
	//	RongIM.getInstance().startConversation(getActivity(), ConversationType.PRIVATE, MyApplication.getInstance().getCurrentLoginedUser().getUserId()+"", "");

	Intent intent = new Intent();
	intent.setClass(this, UserInfoTextActivity.class);
	intent.putExtra("user_id", user.getUserId());
	intent.putExtra("is_vip", user.getVip() == 1);
	intent.putExtra("user_nickname", user.getNickname());
	intent.putExtra("user_face_url", user.getFaceUrl().getSmallImageUrl());

	startActivity(intent);
    }

}
