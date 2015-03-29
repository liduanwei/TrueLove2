package me.himi.love.ui.fragment;

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
import me.himi.love.AppServiceImpl;
import me.himi.love.MyApplication;
import me.himi.love.IAppService.OnLoadUserVisitorsListener;
import me.himi.love.IAppService.UserVisitorsParams;
import me.himi.love.IAppServiceExtend.LoadFansParams;
import me.himi.love.IAppServiceExtend.OnLoadFansResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.UserVisitorsAdapter;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.VisitedUser;
import me.himi.love.entity.VisitorUser;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.UserVisitorsActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUtils;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VisitorsWhoseeFragment extends BaseFragment implements OnItemClickListener {
    me.himi.love.view.list.XListView mListView;
    me.himi.love.adapter.UserVisitorsAdapter mAdapter;

    int targetUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Bundle args = getArguments();

	// 放在只创建一次的地方避免重复添加数据
	//	loadUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_whosee, container, false);
	init(view);
	return view;

    }

    List<VisitorUser> data = new ArrayList<VisitorUser>();

    //    ProgressBar pbLoading;// 加载中
    //    TextView tvLoadRetry;// 重试

    private void init(View v) {

	// 加载中
	//	pbLoading = (ProgressBar) v.findViewById(R.id.pb_whosee_loading);
	//	// 加载重试
	//	tvLoadRetry = (TextView) v.findViewById(R.id.tv_whosee_load_retry);

	mListView = (me.himi.love.view.list.XListView) v.findViewById(R.id.listview);
	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mAdapter = new UserVisitorsAdapter(getActivity(), data);
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
	List<VisitorUser> users = CacheUtils.loadFromCache(cacheUsersPath);
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
    private final String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/whosee_users_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    int pageNumber = 1;

    private void loadUsers() {

	if (!ActivityUtil.hasNetwork(this.getActivity())) {
	    ToastFactory.getToast(this.getActivity(), "请开启网络").show();
	    return;
	}

	UserVisitorsParams params = new UserVisitorsParams();
	params.page = pageNumber;
	params.pageSize = 10;
	params.userId = targetUserId;

	AppServiceImpl.getInstance().loadUserVisitors(params, new OnLoadUserVisitorsListener() {

	    @Override
	    public void onSuccess(List<VisitorUser> users) {

		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);

		    // 缓存到本地
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath);

		} else {

		    if (mAdapter.getList().size() == 0) {
			ActivityUtil.show(getActivity(), "暂无访客");
		    } else {
			ToastFactory.getToast(getActivity(), "暂无更多访客").show();
		    }

		}
		//

		pageNumber++;

		mListView.stopLoadMore();
		mListView.stopRefresh();

		//		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(getActivity(), errorMsg);
		mListView.stopLoadMore();
		mListView.stopRefresh();

		//		tvLoadRetry.setVisibility(View.VISIBLE);

	    }
	});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	position = position - 1;//mListView.getHeaderViewsCount();

	if (position <= -1 || position >= mAdapter.getList().size()) {
	    return;
	}
	VisitorUser user = mAdapter.getList().get(position);

	Intent intent = new Intent();
	intent.setClass(this.getActivity(), UserInfoTextActivity.class);
	intent.putExtra("user_id", user.getUserId());
	intent.putExtra("user_nickname", user.getNickname());
	intent.putExtra("user_face_url", user.getFaceUrl());
	startActivity(intent);

	getActivity().finish();// 结束掉,防止回退栈过深

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
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
	super.onDestroy();
    }

}
