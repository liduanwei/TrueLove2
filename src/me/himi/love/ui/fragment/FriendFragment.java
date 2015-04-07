package me.himi.love.ui.fragment;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadFriendsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadFriendsResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.MyFriendsAdapter;
import me.himi.love.entity.FriendUser;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 互相关注即为好友
 * @author sparklee
 *
 */
public class FriendFragment extends BaseFragment implements OnItemClickListener {
    private static final String TAG = "TestFragment";

    private me.himi.love.view.list.XListView mListView;

    private MyFriendsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.d(TAG, "TestFragment-----onCreate");
	Bundle args = getArguments();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "TestFragment-----onCreateView");
	View view = inflater.inflate(R.layout.fragment_friends, container, false);

	// 为什么会出现数据重复添加的情况呢?原来是生命周期没弄明白, 有些方法会被多次调用
	init(view);

	return view;

    }

    private int pageNumber = 1;

    private void init(View view) {

	mListView = (me.himi.love.view.list.XListView) view.findViewById(R.id.listview_friends);

	mListView.setPullRefreshEnable(false);
	mListView.setPullLoadEnable(false);

	mAdapter = new MyFriendsAdapter(getActivity(), data);

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

	loadFriends();

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

    private void loadFriends() {
	LoadFriendsPostParams postParams = new LoadFriendsPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	postParams.longtitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();

	AppServiceExtendImpl.getInstance().loadFriends(postParams, new OnLoadFriendsResponseListener() {

	    @Override
	    public void onSuccess(List<FriendUser> users) {
		// TODO Auto-generated method stub
		if (users == null) {
		    return;
		}
		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(users);
		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无好友");
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

    List<FriendUser> data = new ArrayList<FriendUser>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
    }

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

	RongIM.getInstance().startConversationList(this.getActivity());
//	RongIM.getInstance().startPrivateChat(getActivity(), user.getUserId() + "", user.getNickname());
	//	RongIM.getInstance().startConversation(getActivity(), ConversationType.PRIVATE, MyApplication.getInstance().getCurrentLoginedUser().getUserId()+"", "");

    }

}
