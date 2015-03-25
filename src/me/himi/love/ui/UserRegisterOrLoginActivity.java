package me.himi.love.ui;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserRegisterOrLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_user_registerorlogin);

	init();

    }

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText(getResources().getString(R.string.app_name));
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setVisibility(View.GONE);
	tvTopAction.setText(getResources().getString(R.string.app_name));

    }

    public void btnStartRegisterOnClick(View v) {
	startActivity(new Intent(this, UserRegisterActivity.class));
    }

    public void btnStartLoginOnClick(View v) {
	startActivity(new Intent(this, UserLoginActivity.class));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

	return super.onKeyDown(keyCode, event);
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (firstTime + 2000 > System.currentTimeMillis()) {
	    MyApplication.getInstance().exit();
	    super.onBackPressed();
	} else {
	    ActivityUtil.show(this, "再按一次退出程序");
	}
	firstTime = System.currentTimeMillis();
    }

}
