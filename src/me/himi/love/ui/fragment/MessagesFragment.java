package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.MessagesFragmentPagerAdapter;
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
 * 消息栏 包含 私信,聊天会话,系统消息3类
 * @author sparklee
 *
 */
public class MessagesFragment extends BaseFragment implements OnClickListener {
    private static final String TAG = "TestFragment";

    private ViewPager mViewPager;
    private View mCursorLine;

    private TextView[] tvTabs;
    private TextView mTvPrivateMsg, mTvSystemMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.d(TAG, "TestFragment-----onCreate");
	Bundle args = getArguments();

	// 放在只创建一次的地方避免重复添加数据

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "TestFragment-----onCreateView");
	View view = inflater.inflate(R.layout.fragment_messages, container, false);
	init(view);
	return view;
    }

    private int currentPageIndex;
    private int cursorLineWidth;

    Fragment privateMsgFragment;
    //    Fragment chatFragment;
//    Fragment systemMsgFragment;

    private void init(View v) {
	mTvPrivateMsg = (TextView) v.findViewById(R.id.tv_tab_private_message);
	mTvSystemMsg = (TextView) v.findViewById(R.id.tv_tab_system_message);
	mTvPrivateMsg.setOnClickListener(this);
	mTvSystemMsg.setOnClickListener(this);

	tvTabs = new TextView[] { mTvPrivateMsg, mTvSystemMsg };
	tvTabs[0].setTextColor(getResources().getColor(R.color.cursor_line));

	mViewPager = (ViewPager) v.findViewById(R.id.vPager);
	mCursorLine = v.findViewById(R.id.iv_bottom_line);

	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = ActivityUtil.getScreenSize()[0] / 2;
	cursorLineWidth = params.width;
	mCursorLine.setLayoutParams(params);

	// 创建viewpager中包含的 fragment
	List<Fragment> fragments = new ArrayList<Fragment>();

	privateMsgFragment = new PrivateMessageFragment(); // 私信消息
	//	chatFragment = new ChatMessagesFragment(); // 会话消息
//	systemMsgFragment = new SystemMessagesFragment(); // 系统消息
	fragments.add(privateMsgFragment);
	//	fragments.add(chatFragment);
//	fragments.add(systemMsgFragment);

	mViewPager.setAdapter(new MessagesFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragments));

	mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

	    @Override
	    public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		TranslateAnimation animation = null;
		switch (arg0) {
		case 0:
		    animation = new TranslateAnimation(currentPageIndex * cursorLineWidth, 0, 0, 0);
		    break;
		case 1:
		    animation = new TranslateAnimation(currentPageIndex * cursorLineWidth, cursorLineWidth, 0, 0);
		    break;
		}
		tvTabs[currentPageIndex].setTextColor(getResources().getColor(R.color.base_color_text_black));

		animation.setFillAfter(true);
		animation.setDuration(300);
		mCursorLine.startAnimation(animation);
		currentPageIndex = arg0;

		tvTabs[currentPageIndex].setTextColor(getResources().getColor(R.color.cursor_line));

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
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "TestFragment-----onDestroy");
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	int index = 0;
	switch (v.getId()) {
	case R.id.tv_tab_private_message:
	    index = 0;
	    break;
	case R.id.tv_tab_system_message:
	    index = 1;
	    break;
	}

	mViewPager.setCurrentItem(index);
	currentPageIndex = index;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
	if (!hidden) {
	    // 隐藏 消息提示
	    getActivity().findViewById(R.id.tv_message_tips).setVisibility(View.GONE);
	}
	super.onHiddenChanged(hidden);
    }

    /**
     * 供外部获知当前所处页面
     * @return
     */
    public int getCurrentPageIndex() {
	return this.currentPageIndex;
    }
}
