package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 * @ClassName:EmailRegisterActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class EmailRegisterActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_register_email);

	init();

    }

    private void init() {
	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	TextView tvTopAction = getViewById(R.id.tv_top_action);

	tvTopTitle.setText("使用邮箱注册");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("");

    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_pref_step:
	    finish();
	    break;
	case R.id.tv_next_step:
	    
	    break;
	}
    }

    public void regByPhoneNumberOnClick(View v) {
	startActivity(new Intent(this, PhoneNumberRegisterActivity.class));
	finish();
    }

}
