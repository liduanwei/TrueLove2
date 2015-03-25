package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadUnreadFansParams;
import me.himi.love.IAppServiceExtend.OnLoadUnreadFansResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.MyReceivedFansAdapter;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用户收到的新粉丝
 * @ClassName:MyReceivedFansActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyReceivedFansActivity extends BaseActivity implements OnItemClickListener {

    TextView tvTopTitle, tvTopAction;
    me.himi.love.view.list.XListView mListView;

    MyReceivedFansAdapter mAdapter;

    private int pageNumber = 1;

    RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	mContentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_my_fans_messages, null);
	setContentView(mContentView);

	init();

    }

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

	    }
	});

	mListView = (XListView) findViewById(R.id.listview_my_fans_msges);
	mListView.setPullRefreshEnable(true);
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadUnreadFans();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadUnreadFans();
	    }
	});

	mAdapter = new MyReceivedFansAdapter(this, new ArrayList<ReceivedFans>());

	mListView.setAdapter(mAdapter);

	mListView.setOnItemClickListener(this);

	// 所有未读的新粉丝
	loadUnreadFans();

    }

    View mLoadingView;

    private void loadUnreadFans() {

	if (mLoadingView == null) {
	    mLoadingView = getLayoutInflater().inflate(R.layout.layout_loading_retry, null);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT);
	    mContentView.addView(mLoadingView, params);
	    mLoadingView.findViewById(R.id.tv_load_retry).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    loadUnreadFans();

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);
	// TODO Auto-generated method stub
	LoadUnreadFansParams postParams = new LoadUnreadFansParams();
	postParams.page = pageNumber;
	postParams.pageSize = 10;
	AppServiceExtendImpl.getInstance().loadUnreadFans(postParams, new OnLoadUnreadFansResponseListener() {

	    @Override
	    public void onSuccess(List<ReceivedFans> fans) {

		if (pageNumber == 1) {
		    mAdapter.getList().clear();
		}
		mAdapter.addAll(fans);

		mListView.stopRefresh();
		mListView.stopLoadMore();

		pageNumber++;

		// 隐藏
		mLoadingView.setVisibility(View.GONE);

	    }

	    @Override
	    public void onFailure(String errorMsg) {

		mListView.stopRefresh();
		mListView.stopLoadMore();

		showToast(errorMsg);

		// 重试按钮可见
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);

	    }
	});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	ReceivedFans fans = mAdapter.getList().get(position - 1);

	Intent intent = new Intent();
	intent.setClass(this, UserInfoTextActivity.class);
	intent.putExtra("user_id", fans.getUserId());
	intent.putExtra("user_nickname", fans.getNickname());
	intent.putExtra("user_face_url", fans.getFaceUrl());
	startActivity(intent);
    }

}
