package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.ContactsFragmentPagerAdapter;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.ActivityUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class ContactsFragment extends BaseFragment implements OnClickListener {

    View mContainerView;

    ViewPager mViewPager;
    View mCursorLine;

    TextView[] tvTabs;
    TextView tvFriends, tvFollow, tvFans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	Log.d(getClass().getSimpleName(), "->onCreateView");
	mContainerView = inflater.inflate(R.layout.fragment_contacts, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);

	lineWidth = ActivityUtil.getScreenSize()[0] / 3;
	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = lineWidth;
	mCursorLine.setLayoutParams(params);
    }

    private int mCurrentPageIndex;
    private int lineWidth;

    private void init() {
	tvFriends = (TextView) mContainerView.findViewById(R.id.tv_tab_friends);
	tvFollow = (TextView) mContainerView.findViewById(R.id.tv_tab_follow);
	tvFans = (TextView) mContainerView.findViewById(R.id.tv_tab_fans);

	tvTabs = new TextView[] { tvFriends, tvFollow, tvFans };
	tvTabs[0].setTextColor(getResources().getColor(R.color.cursor_line));

	tvFriends.setOnClickListener(this);
	tvFollow.setOnClickListener(this);
	tvFans.setOnClickListener(this);

	mCursorLine = mContainerView.findViewById(R.id.iv_nearby_bottom_line);

	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = ActivityUtil.getScreenSize()[0] / 3;
	lineWidth = params.width;
	mCursorLine.setLayoutParams(params); // 设置光标线宽度

	mViewPager = (ViewPager) mContainerView.findViewById(R.id.nearbyPager);
	Fragment followFragment = new FollowFragment();
	Fragment fansFragment = new FansFragment();
	Fragment friendFragment = new FriendFragment();

	List<Fragment> fragments = new ArrayList<Fragment>();

	fragments.add(friendFragment);
	fragments.add(followFragment);
	fragments.add(fansFragment);

	mViewPager.setAdapter(new ContactsFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragments));
	mViewPager.setCurrentItem(0);

	mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

	    @Override
	    public void onPageSelected(int arg0) {
		TranslateAnimation anim = null;
		switch (arg0) {
		case 0:
		    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, 0, 0, 0);
		    break;
		case 1:
		    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, lineWidth, 0, 0);
		    break;
		case 2:
		    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, 2 * lineWidth, 0, 0);
		    break;
		}
		tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.base_color_text_black));
		//		anim.setInterpolator(new CycleInterpolator(0.2f));
		anim.setDuration(300);
		anim.setFillAfter(true); // 停留在最后一帧
		mCursorLine.startAnimation(anim);
		mCurrentPageIndex = arg0;

		tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.cursor_line));
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	    }
	});

    }

    @Override
    public void onHiddenChanged(boolean hidden) {

	super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	int index = 0;
	switch (v.getId()) {
	case R.id.tv_tab_friends:
	    index = 0;
	    break;
	case R.id.tv_tab_follow:
	    index = 1;
	    break;
	case R.id.tv_tab_fans:
	    index = 2;
	    break;
	}
	mViewPager.setCurrentItem(index);
	mCurrentPageIndex = index;
    }
}
