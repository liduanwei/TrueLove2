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
import me.himi.love.IAppServiceExtend.OnPostRecommendUsersResponseListener;
import me.himi.love.IAppServiceExtend.PostRecommendUsersParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.UserWallAdapter;
import me.himi.love.entity.RecommendUser;
import me.himi.love.entity.loader.IRecommendUserLoader;
import me.himi.love.entity.loader.impl.NearbyUserLoaderImpl;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.ui.sound.SoundPlayer;
import me.himi.love.util.CacheUtils;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huewu.pla.lib.MultiColumnListView.OnLoadMoreListener;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class UserWallFragment extends BaseFragment {
    MultiColumnPullToRefreshListView mMultiColListView;
    UserWallAdapter mAdapter;

    IRecommendUserLoader recommendUserLoader = new NearbyUserLoaderImpl();

    View mContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(getClass().getSimpleName(), "->onCreateView");
	mContainerView = inflater.inflate(R.layout.fragment_user_wall, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);

	//	init();
    }

    final OnRefreshListener onRefreshListener = new OnRefreshListener() {

	@Override
	public void onRefresh() {
	    pageNumber = 1;

	    loadRecommendUsers();

	    //	    mMultiColListView.onRefreshComplete();
	}
    };

    View.OnClickListener refreshOnClickListener = new View.OnClickListener() {

	@Override
	public void onClick(View v) {
	    // TODO Auto-generated method stub
	    onRefreshListener.onRefresh();
	}
    };

    TextView tvTopAction;

    private int pageNumber = 1;

    private void init() {

	// 右上角刷新
	tvTopAction = (TextView) getActivity().findViewById(R.id.tv_top_action);

	this.mMultiColListView = (com.huewu.pla.lib.MultiColumnPullToRefreshListView) mContainerView.findViewById(R.id.multiColumPullToRefreshListView);
	// 滚动, 
	this.mMultiColListView.setOnScrollListener(new OnScrollListener() {

	    @Override
	    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
		    // 当前滚动到了最后一行
		    loadRecommendUsers();
		}
	    }
	});

	this.mAdapter = new UserWallAdapter(this.getActivity(), new ArrayList<RecommendUser>());

	this.mMultiColListView.setAdapter(mAdapter);

	// 刷新
	this.mMultiColListView.setOnRefreshListener(onRefreshListener);

	// 滑动到底部自动加载更多
	//	this.mMultiColListView.setOnLoadMoreListener(new OnLoadMoreListener() {
	//
	//	    @Override
	//	    public void onLoadMore() {
	//		showToast("加载更多中.........");
	//		loadRecommendUsers();
	//	    }
	//	});

	// 加载推荐的用户(符合当前用户征婚年龄段的异性,按虚拟币数量和VIP排序)
	//	loadRecommendUsers();

	loadFromCache();
    }

    private void loadFromCache() {
	List<RecommendUser> users = CacheUtils.loadFromCache(cacheUsersPath);
	if (users != null) {
	    mAdapter.setList(users);
	} else {
	    loadRecommendUsers();
	}

    }

    private boolean isRefreshing;// 是否加载中
    // 使用本地缓存
    private final static String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/users_recommend" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    private void loadRecommendUsers() {
	if (isRefreshing)
	    return;
	isRefreshing = true;

	//	Map<String, String> nameAndValues = new HashMap<String, String>();
	//	nameAndValues.put("page", pageNumber + "");
	//	nameAndValues.put("page_size", 20 + "");
	//
	//	RequestParams params = new RequestParams(nameAndValues);
	//
	//	HttpUtil.post(Constants.URL_RECOMMEND_USER, params, new AsyncHttpResponseHandler() {
	//
	//	    @Override
	//	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	//
	//		String response = new String(arg2);
	//
	//		List<NearbyUser> users = recommendUserLoader.load(response);
	//
	//		mMultiColListView.onRefreshComplete();
	//		if (users.size() != 0) {
	//		    if (pageNumber == 1) { // 当前是首页则表示刷新
	//			mUserWallAdapter.getList().clear();
	//		    }
	//
	//		    mUserWallAdapter.addAll(users);
	//		} else {
	//		    // 没有数据或没有更多数据了
	//
	//		}
	//
	//		pageNumber++;
	//
	//	    }
	//
	//	    @Override
	//	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
	//
	//		ActivityUtil.show(getActivity(), "网络超时");
	//	    }
	//	});

	PostRecommendUsersParams postParams = new PostRecommendUsersParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;

	postParams.longtitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();

	AppServiceExtendImpl.getInstance().loadRecommendUsers(postParams, new OnPostRecommendUsersResponseListener() {

	    @Override
	    public void onSuccess(List<RecommendUser> users) {
		// TODO Auto-generated method stub

		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();

			// 刷新加载到数据时播放音乐
			SoundPlayer.getInstance(getActivity()).playOk();
		    }
		    mAdapter.addAll(users);
		    pageNumber++;

		    // 缓存到本地,下次启动首先从缓存加载
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath);

		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无数据");
		}

		// 刷新完成
		isRefreshing = false;

		mMultiColListView.onRefreshComplete();

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		isRefreshing = false;
		mMultiColListView.onRefreshComplete();
		showToast(errorMsg);
	    }
	});
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

	super.onHiddenChanged(hidden);
	//	if (!hidden) {
	//	    tvTopAction.setVisibility(View.VISIBLE);
	//	    tvTopAction.setOnClickListener(refreshOnClickListener);
	//	    tvTopAction.setText("刷新");
	//	}
    }
}
