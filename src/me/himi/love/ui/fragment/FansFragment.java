package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadFansParams;
import me.himi.love.IAppServiceExtend.OnLoadFansResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.UserNearbyAdapter;
import me.himi.love.entity.NearbyUser;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FansFragment extends BaseFragment {
    private me.himi.love.view.list.XListView mListView;
    private UserNearbyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Bundle args = getArguments();

	// 放在只创建一次的地方避免重复添加数据
	loadFansUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_fans, container, false);
	init(view);
	return view;

    }

    List<NearbyUser> data = new ArrayList<NearbyUser>();

    private void init(View v) {
	mListView = (me.himi.love.view.list.XListView) v.findViewById(R.id.listview_fans);
	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mAdapter = new UserNearbyAdapter(getActivity(), data);
	mListView.setAdapter(mAdapter);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadFansUser();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadFansUser();
	    }
	});

    }

    private int pageNumber = 1;

    private boolean isRefreshing;// 刷新中...

    private void loadFansUser() {
	if (isRefreshing)
	    return;
	isRefreshing = true;

	LoadFansParams postParams = new LoadFansParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;

	AppServiceExtendImpl.getInstance().loadFans(postParams, new OnLoadFansResponseListener() {

	    @Override
	    public void onSuccess(List<NearbyUser> users) {
		// TODO Auto-generated method stub
		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);
		} else {

		}
		
		isRefreshing = false;

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		
		
		
		pageNumber++;
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		isRefreshing = false;
		
		
		// TODO Auto-generated method stub
		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		showToast(errorMsg);
	    }
	});
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
