package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.ContactsFragmentPagerAdapter;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.ui.fragment.VisitorsSeewhoFragment;
import me.himi.love.ui.fragment.VisitorsWhoseeFragment;
import me.himi.love.util.ActivityUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * Ta看过谁,谁看过Ta
 * @ClassName:VisitorsActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class VisitorsAndVisitwhoActivity extends BaseActivity implements OnClickListener {

    ViewPager mViewPager;
    View mCursorLine;

    TextView[] tvTabs;
    TextView tvTab1, tvTab2;

    private int mCurrentPageIndex;
    private int lineWidth;

    private int targetUserId;

    public static final String USER_GENDER = "user_gender";

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_visitors);

	init();

    }

    private void init() {

	targetUserId = getIntent().getIntExtra("user_id", 0);

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("访客");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setVisibility(View.GONE);

	tvTab1 = (TextView) findViewById(R.id.tv_tab_1);
	tvTab2 = (TextView) findViewById(R.id.tv_tab_2);

	// 传递过来用户的性别
	int gender = getIntent().getIntExtra(USER_GENDER, 0);

	if (targetUserId == 0) {
	    showToast("没有指定用户");
	} else if (targetUserId == MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	    // 当前用户
	    tvTab1.setText("谁看过我");
	    tvTab2.setText("我看过谁");
	} else {
	    if (gender == 0) {
		tvTab1.setText("谁看过她");
		tvTab2.setText("她看过谁");
	    } else {
		tvTab1.setText("谁看过他");
		tvTab2.setText("他看过谁");
	    }
	}

	tvTabs = new TextView[] { tvTab1, tvTab2 };
	tvTabs[0].setTextColor(getResources().getColor(R.color.cursor_line));

	tvTab1.setOnClickListener(this);
	tvTab2.setOnClickListener(this);

	mCursorLine = findViewById(R.id.iv_nearby_bottom_line);

	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = ActivityUtil.getScreenSize()[0] / 2;
	lineWidth = params.width;
	mCursorLine.setLayoutParams(params); // 设置光标线宽度

	mViewPager = (ViewPager) findViewById(R.id.nearbyPager);

	VisitorsWhoseeFragment whoseeFragment = new VisitorsWhoseeFragment(); // 谁看过ta
	VisitorsSeewhoFragment seewhoFragment = new VisitorsSeewhoFragment(); // ta看过谁

	List<Fragment> fragments = new ArrayList<Fragment>();

	fragments.add(whoseeFragment);
	fragments.add(seewhoFragment);

	whoseeFragment.setTargetUserId(targetUserId); // 加载谁看过
	seewhoFragment.setTargetUserId(targetUserId); // 加载看过谁

	mViewPager.setAdapter(new ContactsFragmentPagerAdapter(getSupportFragmentManager(), fragments));
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
    public void onClick(View v) {
	// TODO Auto-generated method stub
	int index = 0;
	switch (v.getId()) {
	case R.id.tv_tab_1:
	    index = 0;
	    break;
	case R.id.tv_tab_2:
	    index = 1;
	    break;
	}
	mViewPager.setCurrentItem(index);
	mCurrentPageIndex = index;
    }

}
