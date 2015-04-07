package me.himi.love.ui.base;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.util.ActivityManagerUtils;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ToastFactory;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @ClassName:BaseActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:09:31 PM
 */
public class BaseActivity extends FragmentActivity {

    protected int mScreenWidth, mScreenHeight;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);

	DisplayMetrics metric = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(metric);

	this.mScreenWidth = metric.widthPixels;
	this.mScreenHeight = metric.heightPixels;

	// 
	MyApplication.getInstance().addActivity(this);
    }

    public void showToast(final CharSequence text) {
	runOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		// 
		if (ActivityUtil.isBackground(BaseActivity.this)) { // 当前应用处于后台则不显示
		    return;
		}
		ToastFactory.getToast(BaseActivity.this, text.toString()).show();
		//		Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
	    }
	});
    }

    /** 隐藏软键盘
     * hideSoftInputView
     * @Title: hideSoftInputView
     * @Description: TODO
     * @param  
     * @return void
     * @throws
     */
    public void hideSoftInputView() {
	InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
	if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
	    if (getCurrentFocus() != null)
		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
    }

    /**
     *防止横竖屏后出现重叠现象 重写该方法 去掉 super.onSaveInstanceState(outState);
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
	// TODO Auto-generated method stub
	// 防止出现重叠现象
	//        super.onSaveInstanceState(outState);
    }

    // 获取指定id的view  
    protected <T extends View> T getViewById(int id) {
	return (T) findViewById(id);
    }

    @Override
    public void finish() {
	// TODO Auto-generated method stub
	super.finish();
	//	ActivityManagerUtils.getInstance().removeActivity(this);

    }

    /**
     * 后退后删除list中的自身
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	ActivityManagerUtils.getInstance().removeActivity(this);
	super.onBackPressed();
    }

    /**
     * 读取并配置顶部导航栏背景色
     * @param color
     */
    protected void readAndSetTopBackgroundColor() {
	SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

	int color = sp.getInt("back_color", getResources().getColor(R.color.bar));

	View v = findViewById(R.id.layout_nav_top);
	if (v != null) {
	    v.setBackgroundColor(color);
	}
    }

    /**
     * 保存配置
     * @param color
     */
    protected void saveTopBackgroundColor(int color) {
	SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

	sp.edit().putInt("back_color", color).commit();
    }

    protected int readTopBackgroundColor() {
	SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	int color = sp.getInt("back_color", getResources().getColor(R.color.bar));
	return color;
    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	// TODO Auto-generated method stub
	readAndSetTopBackgroundColor();
    }

    
    @Override
    protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
    }
}
