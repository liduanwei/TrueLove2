package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadVisitedUsersPostParams;
import me.himi.love.IAppServiceExtend.OnLoadVisitedUsersResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.VisitedUsersAdapter;
import me.himi.love.entity.VisitedUser;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.CacheUtils;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class VisitorsSeewhoFragment extends BaseFragment implements OnItemClickListener {
    private me.himi.love.view.list.XListView mListView;
    private VisitedUsersAdapter mAdapter;

    private int targetUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Bundle args = getArguments();

	// 放在只创建一次的地方避免重复添加数据
	//	loadUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_seewho, container, false);
	init(view);
	return view;

    }

    List<VisitedUser> data = new ArrayList<VisitedUser>();

    //    ProgressBar pbLoading;// 加载中
    //    TextView tvLoadRetry;// 重试

    private void init(View v) {
	// 加载中...
	//	pbLoading = (ProgressBar) v.findViewById(R.id.pb_loading);
	//	// 重新加载
	//	tvLoadRetry = (TextView) v.findViewById(R.id.tv_load_retry);

	mListView = (me.himi.love.view.list.XListView) v.findViewById(R.id.listview);
	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mAdapter = new VisitedUsersAdapter(getActivity(), data);
	mListView.setAdapter(mAdapter);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadUsers();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadUsers();
	    }
	});

	mListView.setOnItemClickListener(this);

	// 从缓存中加载数据
	List<VisitedUser> users = CacheUtils.loadFromCache(cacheUsersPath + targetUserId);
	if (users != null) {
	    mAdapter.setList(users);
	} else {
	    loadUsers();
	}
    }

    public void setTargetUserId(int targetUserId) {
	this.targetUserId = targetUserId;
	//	loadUsers();

    }

    // 使用本地缓存
    private final String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/seewho_users_";

    private int pageNumber = 1;

    private void loadUsers() {
	LoadVisitedUsersPostParams postParams = new LoadVisitedUsersPostParams();
	postParams.userId = targetUserId;
	postParams.page = pageNumber;
	postParams.pageSize = 20;

	AppServiceExtendImpl.getInstance().loadVisitedUsers(postParams, new OnLoadVisitedUsersResponseListener() {

	    @Override
	    public void onSuccess(List<VisitedUser> users) {
		// TODO Auto-generated method stub
		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);
		    // 缓存到本地
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath + targetUserId);
		} else {

		}

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		pageNumber++;

		//		pbLoading.setVisibility(View.GONE);
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

		//		tvLoadRetry.setVisibility(View.VISIBLE);
	    }
	});
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
	// TODO Auto-generated method stub
	if (!hidden) {
	    //	    tvLoadRetry.setOnClickListener(new OnClickListener() {
	    //
	    //		@Override
	    //		public void onClick(View v) {
	    //		    // TODO Auto-generated method stub
	    //		    loadUsers();
	    //		}
	    //	    });
	}
	super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	position -= 1;

	VisitedUser user = mAdapter.getList().get(position);

	Intent intent = new Intent();
	intent.setClass(this.getActivity(), UserInfoTextActivity.class);
	intent.putExtra("user_id", user.getUserId());
	intent.putExtra("user_nickname", user.getNickname());
	intent.putExtra("user_face_url", user.getFaceUrl());
	startActivity(intent);

	getActivity().finish();// 结束掉,防止回退栈过深
    }

}
