package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import android.os.Bundle;
import android.view.Window;

/**
 * @ClassName:UsersSearchActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UsersSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_users_search);

	init();
    }

    private void init() {

    }

}
