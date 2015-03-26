package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName:FirstGuideActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class FirstGuideActivity extends BaseActivity {

    // 首次启动 滑动引导页

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);

	SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

	if (sp.getBoolean("is_first", true)) { // 首次启动
	    sp.edit().putBoolean("is_first", false).commit();
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_first_guide);
	    init();
	} else {
	    // 直接进入开屏页面
	    startActivity(new Intent(FirstGuideActivity.this, SplashActivity.class));
	}

    }

    // 引导图片
    private static final int[] pics = { R.drawable.register, R.drawable.register, R.drawable.register };
    // 指引圆点
    private static ImageView[] dots;

    private int currentIndex;

    private void init() {

	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	tvTopTitle.setText("恋恋");
	TextView tvTopAction = getViewById(R.id.tv_top_action);
	tvTopAction.setText("");
	tvTopAction.setVisibility(View.GONE);

	mViewPager = (ViewPager) findViewById(R.id.viewpager);
	dots = new ImageView[3];
	dots[0] = getViewById(R.id.iv_dot1);
	dots[1] = getViewById(R.id.iv_dot2);
	dots[2] = getViewById(R.id.iv_dot3);

	List<View> views = new ArrayList<View>();

	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

	for (int i = 0, n = pics.length; i < n; ++i) {
	    ImageView iv = new ImageView(this);
	    iv.setImageResource(pics[i]);
	    iv.setScaleType(ScaleType.CENTER);
	    iv.setLayoutParams(params);
	    views.add(iv);
	    dots[i].setEnabled(false);
	}
	dots[0].setEnabled(true);

	//
	views.get(views.size() - 1).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 进入开屏页
		startActivity(new Intent(FirstGuideActivity.this, SplashActivity.class));
	    }
	});

	mViewPager.setAdapter(new FirstPagerAdapter(views));

	mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

	    @Override
	    public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		if (currentIndex != arg0) {
		    dots[currentIndex].setEnabled(false);
		    currentIndex = arg0;
		    dots[currentIndex].setEnabled(true);
		}

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

    class FirstPagerAdapter extends PagerAdapter {

	List<View> views;

	public FirstPagerAdapter(List<View> views) {
	    this.views = views;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    // TODO Auto-generated method stub
	    container.removeView(views.get(position));
	}

	@Override
	public int getCount() {
	    // TODO Auto-generated method stub
	    return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
	    // TODO Auto-generated method stub
	    return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
	    // TODO Auto-generated method stub
	    View v = views.get(position);
	    container.addView(v);
	    return v;
	}
    }

    /**
     * 进入注册
     * @param v
     */
    public void btnStartRegisterOnClick(View v) {
	startActivity(new Intent(this, UserRegisterActivity.class));
    }

    /**
     * 进入登录
     * @param v
     */
    public void btnStartLoginOnClick(View v) {
	startActivity(new Intent(this, UserLoginActivity.class));
    }

}
