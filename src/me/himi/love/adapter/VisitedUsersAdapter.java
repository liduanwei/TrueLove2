package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.VisitedUser;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.EmojiTextView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

public class VisitedUsersAdapter extends BaseListAdapter<VisitedUser> {

    public VisitedUsersAdapter(Context context, List<VisitedUser> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.visited_user_item, null);
	}

	final VisitedUser user = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);

	EmojiTextView tvNickname = ViewHolder.get(convertView, R.id.tv_user_nickname);
	TextView tvAge = ViewHolder.get(convertView, R.id.tv_user_age);
	// 访问时间
	TextView tvVisitTime = ViewHolder.get(convertView, R.id.tv_user_visitor_visittime);

	tvVisitTime.setText(user.getVisitTime());

	// displayImage 会有刷新闪烁问题
	ImageLoader.getInstance().displayImage(user.getFaceUrl(), ivFace, ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {

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
		// TODO Auto-generated method stub
		notifyDataSetChanged();
	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});

	tvNickname.setEmojiText(user.getNickname());
	tvAge.setText(user.getAge() + "");

	int backRes = 0;
	Drawable genderDrawable = null;
	if (user.getGender() == 1) {
	    backRes = R.drawable.shape_gender_age_male;
	    genderDrawable = mContext.getResources().getDrawable(R.drawable.ic_user_male2);
	} else {
	    backRes = R.drawable.shape_gender_age_female;
	    genderDrawable = mContext.getResources().getDrawable(R.drawable.ic_user_famale2);
	}
	tvAge.setBackgroundResource(backRes);

	genderDrawable.setBounds(0, 0, genderDrawable.getMinimumWidth(), genderDrawable.getMinimumHeight());
	tvAge.setCompoundDrawables(genderDrawable, null, null, null);

	return convertView;
    }
    //    class ViewHolder {
    //	ImageView ivFace;
    //	TextView tvNickname;
    //	TextView tvAge;
    //	TextView tvHeight;
    //	TextView tvWeight;
    //	TextView tvMonthlySalary;
    //	LinearLayout layoutInterests;
    //	LinearLayout layoutPersonality;
    //    }

}
