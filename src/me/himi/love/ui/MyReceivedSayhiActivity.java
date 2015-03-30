package me.himi.love.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.MyApplication;
import me.himi.love.IAppServiceExtend.LoadSayHiParams;
import me.himi.love.IAppServiceExtend.OnLoadSayHiResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.MyReceivedSayhiAdapter;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.ReceivedSayHi;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.CacheUtils;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
public class MyReceivedSayhiActivity extends BaseActivity implements OnItemClickListener {

    TextView tvTopTitle, tvTopAction;
    TextView tvEmptyTips;
    me.himi.love.view.list.XListView mListView;

    MyReceivedSayhiAdapter mSayhiAdapter;

    private int pageNumber = 1;

    RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mContentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_my_sayhi_messages, null);

	setContentView(mContentView);

	init();

    }

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopTitle.setText("打招呼");
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

	    }
	});

	tvEmptyTips = (TextView) findViewById(R.id.tv_my_sayhi_mesages_empty);

	mListView = (XListView) findViewById(R.id.listview_my_sayhi_msges);
	mListView.setPullRefreshEnable(true);
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadSayhis();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadSayhis();
	    }
	});

	mSayhiAdapter = new MyReceivedSayhiAdapter(this, new ArrayList<ReceivedSayHi>());

	mListView.setAdapter(mSayhiAdapter);

	mListView.setOnItemClickListener(this);

	//	loadSayhis();
	// 从缓存加载

	List<ReceivedSayHi> his = CacheUtils.loadFromCache(cacheUsersPath);
	if (his == null) {
	    loadSayhis();
	} else {
	    mSayhiAdapter.setList(his);
	}

    }

    // 使用本地缓存
    private final String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/mysayhis_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    View mLoadingView;

    private boolean isRefreshing;// 刷新中

    private void loadSayhis() {
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
		    loadSayhis();

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);

	// TODO Auto-generated method stub
	LoadSayHiParams postParams = new LoadSayHiParams();
	postParams.page = pageNumber;
	postParams.pageSize = 10;
	AppServiceExtendImpl.getInstance().loadSayhi(postParams, new OnLoadSayHiResponseListener() {

	    @Override
	    public void onSuccess(List<ReceivedSayHi> sayhis) {

		if (pageNumber == 1) {
		    mSayhiAdapter.getList().clear();
		}
		mSayhiAdapter.addAll(sayhis);

		// 缓存到本地
		CacheUtils.cacheToLocal(mSayhiAdapter.getList(), cacheUsersPath);


		pageNumber++;

		if (mSayhiAdapter.getList().size() != 0) {
		    tvEmptyTips.setVisibility(View.GONE);
		} else {
		    tvEmptyTips.setVisibility(View.VISIBLE);
		}

		mLoadingView.setVisibility(View.GONE);

		mListView.stopLoadMore();
		mListView.stopRefresh();

		isRefreshing = false;

	    }

	    @Override
	    public void onFailure(String errorMsg) {

		mListView.stopRefresh();
		mListView.stopLoadMore();

		showToast(errorMsg);

		isRefreshing = false;
		// 重试按钮隐藏
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
	    }
	});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	position -= 1;
	ReceivedSayHi hi = mSayhiAdapter.getList().get(position);
	Intent intent = new Intent();
	intent.setClass(this, UserInfoTextActivity.class);
	intent.putExtra("user_id", hi.getUserId());
	intent.putExtra("user_nickname", hi.getNickname());
	intent.putExtra("user_face_url", hi.getFaceUrl());
	startActivity(intent);
    }
}
