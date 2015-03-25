package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.RecommendUser;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.util.ImageLoaderOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserRecommendAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class UserWallAdapter extends BaseListAdapter<RecommendUser> {

    public UserWallAdapter(Context context, List<RecommendUser> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.user_wall_item, null);
	    //viewHolder = new ViewHolder();
	    // convertView.setTag(viewHolder);
	}

	final RecommendUser user = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);
	final TextView tvAge = ViewHolder.get(convertView, R.id.tv_user_age);

	tvAge.setText(user.getAge() + "岁");

	// display 会有刷新闪烁问题
	//	ImageLoader.getInstance().displayImage(user.getFaceUrl(), ivFace);

	ivFace.setTag(user.getFaceUrl());

	// 图片闪烁
	ImageLoader.getInstance().displayImage(user.getFaceUrl().getSmallImageUrl(), ivFace,ImageLoaderOptions.normalOptions());

	// 图片错位
	//	ImageLoader.getInstance().loadImage(user.getFaceUrl(), ImageLoaderOptions.normalOptions(), new ImageLoadingListener() {
	//
	//	    @Override
	//	    public void onLoadingStarted(String arg0, View arg1) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//
	//	    @Override
	//	    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//
	//	    @Override
	//	    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
	//		if (ivFace.getTag().equals(user.getFaceUrl())) {
	//		    ivFace.setImageBitmap(arg2);
	//		    // 不能显示的问题
	//		    // notifyDataSetChanged();
	//		}
	//	    }
	//
	//	    @Override
	//	    public void onLoadingCancelled(String arg0, View arg1) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//	});

	// 点击图片
	ivFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(mContext, UserInfoTextActivity.class);
		intent.putExtra("user_id", user.getUserId());
		intent.putExtra("user_nickname", user.getNickname());
//		intent.putExtra("user_face_url", user.getFaceUrl());
		intent.putExtra("user_face_url", user.getFaceUrl().getSmallImageUrl());
		mContext.startActivity(intent);

	    }
	});
	return convertView;
    }
}
