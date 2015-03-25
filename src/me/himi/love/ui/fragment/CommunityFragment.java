package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.ContactsFragmentPagerAdapter;
import me.himi.love.ui.EditArticleActivity;
import me.himi.love.ui.EditQiushiActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.ActivityUtil;
import android.content.Intent;
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
public class CommunityFragment extends BaseFragment implements OnClickListener {

    View mContainerView;

    ViewPager mViewPager;
    View mCursorLine;

    TextView[] tvTabs;
    TextView tvEmbarrassment, tvTucao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	Log.e(getClass().getSimpleName(), "->onCreateView");
	mContainerView = inflater.inflate(R.layout.fragment_share, container, false);
	init();
	return mContainerView;
    }

    TextView tvTopAction;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);

	lineWidth = ActivityUtil.getScreenSize()[0] / 2;
	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = lineWidth;
	mCursorLine.setLayoutParams(params);
    }

    private int mCurrentPageIndex;
    private int lineWidth;

    Fragment embarrassmentfollowFragment; // 糗事
    Fragment secretFragment; // 秘密

    String[] actions = { "发糗事", "发吐槽" };
    
    // 发糗事
    OnClickListener editQiushiListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    // TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), EditQiushiActivity.class);
	    startActivity(intent);
	}
    };

    // 发吐槽
    OnClickListener editTucaoListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    // TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), EditArticleActivity.class);
	    startActivity(intent);
	}
    };

    OnClickListener[] actionListeners = { editQiushiListener, editTucaoListener };

    private void init() {
	tvTopAction = (TextView) getActivity().findViewById(R.id.tv_top_action);

	tvEmbarrassment = (TextView) mContainerView.findViewById(R.id.tv_tab_embarrassment);
	tvTucao = (TextView) mContainerView.findViewById(R.id.tv_tab_tucao);

	tvTabs = new TextView[] { tvEmbarrassment, tvTucao };
	tvTabs[0].setTextColor(getResources().getColor(R.color.cursor_line));

	tvEmbarrassment.setOnClickListener(this);
	tvTucao.setOnClickListener(this);

	mCursorLine = mContainerView.findViewById(R.id.iv_nearby_bottom_line);

	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = ActivityUtil.getScreenSize()[0] / 2;
	lineWidth = params.width;
	mCursorLine.setLayoutParams(params); // 设置光标线宽度

	mViewPager = (ViewPager) mContainerView.findViewById(R.id.sharePager);
	embarrassmentfollowFragment = new EmbarrassmentFragment();
	secretFragment = new ArticleFragment();

	List<Fragment> fragments = new ArrayList<Fragment>();

	fragments.add(embarrassmentfollowFragment);
	fragments.add(secretFragment);

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
		}
		tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.base_color_text_black));
		//		anim.setInterpolator(new CycleInterpolator(0.2f));
		anim.setDuration(300);
		anim.setFillAfter(true); // 停留在最后一帧
		mCursorLine.startAnimation(anim);
		mCurrentPageIndex = arg0;

		tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.cursor_line));

		// 顶部导航栏 操作事件
		TextView tvTopAction = (TextView) getActivity().findViewById(R.id.tv_top_action);
		tvTopAction.setText(actions[mCurrentPageIndex]);
		tvTopAction.setOnClickListener(actionListeners[mCurrentPageIndex]);
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

    /**
     * 发布新帖子
     */
    private View.OnClickListener editOnClickListener = new View.OnClickListener() {

	@Override
	public void onClick(View v) {
	    // TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), EditArticleActivity.class);
	    startActivity(intent);
	}
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
	if (!hidden) {
	    if (tvTopAction != null) {
		tvTopAction.setOnClickListener(editOnClickListener);
		tvTopAction.setVisibility(View.VISIBLE);
		tvTopAction.setText("+");

	    }
	}
	super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	int index = 0;
	switch (v.getId()) {
	case R.id.tv_tab_embarrassment:
	    index = 0;
	    break;
	case R.id.tv_tab_tucao:
	    index = 1;
	    break;
	}
	mViewPager.setCurrentItem(index);
	mCurrentPageIndex = index;
    }
}
