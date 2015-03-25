package me.himi.love.ui.fragment;

import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.HttpUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName:UserInfoPage3Fragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class UserInfoPage3Fragment extends BaseFragment {

    View mContainerView;

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
    }

    private DetailInfoUser mTargetUser;

    public void setInfos(final DetailInfoUser user) {
	mTargetUser = user;

    }

    @Override
    public void onDestroy() {

	HttpUtil.getClient().cancelAllRequests(true);
	super.onDestroy();
    }
}
