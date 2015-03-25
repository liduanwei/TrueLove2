package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.UserPhoto;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:UserRecommendAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class UserPhotosAdapter extends BaseListAdapter<UserPhoto> {

    public UserPhotosAdapter(Context context, List<UserPhoto> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.user_photo_item, null);
	    //viewHolder = new ViewHolder();
	    // convertView.setTag(viewHolder);
	}

	final UserPhoto photo = this.list.get(position);

	final ImageView ivPhoto = ViewHolder.get(convertView, R.id.iv_user_photo);
	final TextView tvLove = ViewHolder.get(convertView, R.id.tv_user_photo_love);

	// display 会有刷新闪烁问题
	//	    ImageLoader.getInstance().displayImage(user.getFaceUrl(), ivFace);
	ImageLoader.getInstance().loadImage(photo.getUrl(), new ImageLoadingListener() {

	    @Override
	    public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		ivPhoto.setImageBitmap(arg2);
	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});

	//	ivFace.setOnClickListener(new View.OnClickListener() {
	//
	//	    @Override
	//	    public void onClick(View v) {
	//		Intent intent = new Intent(mContext, UserInfoActivity.class);
	//		intent.putExtra("user_id", user.getUserId());
	//		intent.putExtra("user_nickname", user.getNickname());
	//		intent.putExtra("user_face_url", user.getFaceUrl());
	//		mContext.startActivity(intent);
	//
	//	    }
	//	});
	return convertView;
    }
}
