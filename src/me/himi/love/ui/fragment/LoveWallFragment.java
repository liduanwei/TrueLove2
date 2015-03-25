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
 * 爱墙,表白墙
 * @ClassName:LoveWallFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class LoveWallFragment extends BaseFragment {

    View mContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(getClass().getSimpleName(), "->onCreateView");
	mContainerView = inflater.inflate(R.layout.fragment_love_wall, container, false);
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);

	init();
    }

    final OnRefreshListener onRefreshListener = new OnRefreshListener() {

	@Override
	public void onRefresh() {
	    pageNumber = 1;

	    loadLoves(); //

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
	loadFromCache();
    }

    private void loadFromCache() {
	// TODO Auto-generated method stub
	File f = new File(cacheUsersPath);
	if (f.exists()) {
	    try {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		Object obj = ois.readObject();
		//		List<RecommendUser> users = (List<RecommendUser>) obj;
		//		mAdapter.getList().clear();
		//		mAdapter.addAll(users);
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
	    loadLoves();
	}
    }

    private boolean isRefreshing;// 是否加载中
    // 使用本地缓存
    private final static String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/users_recommend";

    private void loadLoves() {
	if (isRefreshing)
	    return;
	isRefreshing = true;

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
