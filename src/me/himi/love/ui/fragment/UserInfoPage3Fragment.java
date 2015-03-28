package me.himi.love.ui.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.FindGiftsByUserIdPostParams;
import me.himi.love.IAppServiceExtend.OnFindGiftsByUserIdResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.UserGiftsAdapter;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.UserGift;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * @ClassName:UserInfoPage3Fragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class UserInfoPage3Fragment extends BaseFragment {

    View mContainerView;

    DetailInfoUser mTargetUser;

    UserGiftsAdapter mAdapter;

    List<UserGift> gifts = new ArrayList<UserGift>();

    me.himi.love.view.list.XListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	mContainerView = inflater.inflate(R.layout.userinfo_page3, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    private void init() {
	mListView = (XListView) mContainerView.findViewById(R.id.listview);
	//礼物 adapter
	mAdapter = new UserGiftsAdapter(getActivity(), gifts);
	mListView.setAdapter(mAdapter);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		if (mTargetUser != null) {
		    pageNumber = 1;
		    loadUserGifts(mTargetUser.getUserId() + "");
		}
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		if (mTargetUser != null) {
		    loadUserGifts(mTargetUser.getUserId() + "");
		}
	    }
	});

	// 解决scrollview 嵌套 listview 不能滑动的问题
	// 父 scrollView 容器
	final ScrollView svContainer = (ScrollView) getActivity().findViewById(R.id.sv_center);

	mListView.setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    svContainer.requestDisallowInterceptTouchEvent(true); // 让父scrollview不拦截触摸事件
		    break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		    svContainer.requestDisallowInterceptTouchEvent(false);
		    break;
		}
		return false;
	    }
	});

    }

    public void load(DetailInfoUser userInfo) {

	mTargetUser = userInfo;

	loadUserGiftsFromCache(userInfo.getUserId() + ""); // 加载用户的所有礼物
    }

    // 使用本地缓存
    private final static String cacheUserGiftsPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/user_gifts_";

    /**
     * 
     * 
     */
    private void loadUserGiftsFromCache(String userId) {
	// TODO Auto-generated method stub
	File f = new File(cacheUserGiftsPath + userId);

	if (f.exists()) {

	    try {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

		Object obj = ois.readObject();

		List<UserGift> userGifts = (List<UserGift>) obj;

		mAdapter.getList().clear();

		mAdapter.addAll(userGifts);

		ois.close();

	    } catch (StreamCorruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    // 不存在则从网络获取
	    loadUserGifts(userId);
	}

    }

    private int pageNumber = 1;
    private boolean isRefreshing;// 刷新中

    private void loadUserGifts(String userId) {

	if (isRefreshing) {
	    return;
	}

	isRefreshing = true;

	FindGiftsByUserIdPostParams postParams = new FindGiftsByUserIdPostParams();
	postParams.toUserId = userId;
	postParams.page = 1;
	postParams.pageSize = 100;

	AppServiceExtendImpl.getInstance().findGiftsByUserId(postParams, new OnFindGiftsByUserIdResponseListener() {

	    @Override
	    public void onSuccess(List<UserGift> gifts) {
		// TODO Auto-generated method stub
		showToast("礼物数:" + gifts.size());
		if (gifts.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.setList(gifts);
			//			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(gifts);
		} else {
		    showToast("暂无数据");
		}

		isRefreshing = false;

		mListView.stopRefresh();
		mListView.stopLoadMore();

		pageNumber++;
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		isRefreshing = false;
		mListView.stopRefresh();
		mListView.stopLoadMore();

	    }
	});
    }

    @Override
    public void onDestroy() {

	super.onDestroy();
    }
}
