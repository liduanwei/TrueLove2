package me.himi.love.ui;

import java.util.ArrayList;

import me.himi.love.R;
import me.himi.love.adapter.UserPhotosAdapter;
import me.himi.love.entity.UserPhoto;
import me.himi.love.ui.base.BaseActivity;
import android.os.Bundle;
import android.view.Window;

import com.huewu.pla.lib.MultiColumnPullToRefreshListView;

/**
 * @ClassName:UserPhotosActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserInfoPhotosActivity extends BaseActivity {
    int mUserId; // 目标用户ID

    MultiColumnPullToRefreshListView mPhotosListView;

    UserPhotosAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_user_photos);

	init();

    }

    private void init() {
	this.mPhotosListView = (MultiColumnPullToRefreshListView) findViewById(R.id.multiColumPullToRefreshListView);
	this.mPhotoAdapter = new UserPhotosAdapter(this, new ArrayList<UserPhoto>());

	this.mPhotosListView.setAdapter(mPhotoAdapter);

	for (int i = 0; i < 100; ++i) {
	    UserPhoto photo = null;
	    photo = new UserPhoto();
	    photo.setUrl("http://t11.baidu.com/it/u=1738652632,2779061462&fm=120");
	    this.mPhotoAdapter.add(photo);
	}

    }

    @Override
    protected void onResume() {
	System.out.println("photo onResume");
	super.onResume();
	//        init();
    }

    @Override
    public void onAttachedToWindow() {
	System.out.println("photo onAttactedToWindow");
	super.onAttachedToWindow();
    }

}
