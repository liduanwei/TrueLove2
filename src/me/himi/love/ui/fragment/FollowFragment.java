package me.himi.love.ui.fragment;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.MyApplication;
import me.himi.love.IAppServiceExtend.FollowParams;
import me.himi.love.IAppServiceExtend.LoadFollowParams;
import me.himi.love.IAppServiceExtend.OnFollowResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadFollowResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.UserNearbyAdapter;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.NearbyUser;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FollowFragment extends BaseFragment implements OnItemClickListener {
    private static final String TAG = "TestFragment";

    private me.himi.love.view.list.XListView mListView;

    private UserNearbyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.d(TAG, "TestFragment-----onCreate");
	Bundle args = getArguments();

	// 放在只创建一次的地方避免重复添加数据
	loadFollowUsers();
    }

    private int pageNumber = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "TestFragment-----onCreateView");
	View view = inflater.inflate(R.layout.fragment_follows, container, false);
	init(view);
	return view;

    }

    List<NearbyUser> data = new ArrayList<NearbyUser>();

    private NearbyUser mCurrentSelectedItem;

    private void init(View v) {
	mListView = (me.himi.love.view.list.XListView) v.findViewById(R.id.listview_follow);
	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mAdapter = new UserNearbyAdapter(getActivity(), data);
	mListView.setAdapter(mAdapter);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadFollowUsers();
	    }

	    @Override
	    public void onLoadMore() {
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

	mListView.setOnItemClickListener(this);
    }

    private void loadFollowUsers() {
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
		} else {

		}
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
		    if (-1 == status) { // 还未关注该用户
			mAdapter.getList().remove(mCurrentSelectedItem);
			mAdapter.notifyDataSetChanged();
			return;
		    }
		    // TODO Auto-generated method stub
		    DBHelper db = DBHelper.getInstance(getActivity(), DBHelper.DB_NAME, null, 1);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    //    @Override
    //    public void onHiddenChanged(boolean hidden) {
    //	// TODO Auto-generated method stub
    //	if (!hidden) {
    //	    if (mAdapter.getList().size() == 0) { // 刷新
    //		pageNumber = 1;
    //	    }
    //	    loadFollowUsers();
    //	}
    //	super.onHiddenChanged(hidden);
    //    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "TestFragment-----onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub

	NearbyUser user = mAdapter.getList().get(position - 1);
	RongIM.getInstance().startPrivateChat(getActivity(), user.getUserId() + "", user.getNickname());

    }

}
