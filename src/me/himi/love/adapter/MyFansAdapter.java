package me.himi.love.adapter;

import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.OnSayHiResponseListener;
import me.himi.love.IAppServiceExtend.SayHiParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.NearbyUser;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.ToastFactory;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:UserNearbyAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class MyFansAdapter extends BaseListAdapter<NearbyUser> {

    private Dialog sayhiDialog;
    private View sayhiView;

    public MyFansAdapter(Context context, List<NearbyUser> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.my_fans_item, null);
	}

	final NearbyUser user = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);

	TextView tvNickname = ViewHolder.get(convertView, R.id.tv_user_nickname);
	TextView tvAge = ViewHolder.get(convertView, R.id.tv_user_age);

	ImageView ivVip = ViewHolder.get(convertView, R.id.iv_vip);

	// 距离
//	TextView tvDistance = ViewHolder.get(convertView, R.id.tv_user_distance);

	// vip
	ivVip.setImageResource(user.getVip() != 0 ? R.drawable.vip : R.drawable.vipnot);
	tvNickname.setTextColor(mContext.getResources().getColor(user.getVip() != 0 ? R.color.c_vip : R.color.c_not_vip));
	//	ivVip.setVisibility(user.getVip() != 0 ? View.VISIBLE : View.GONE);

	// displayImage 会有刷新闪烁问题
	ImageLoader.getInstance().displayImage(user.getFaceUrl().getSmallImageUrl(), ivFace, ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {

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

	//	ImageLoader.getInstance().loadImage(user.getFaceUrl(), ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {
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
	//		ivFace.setImageBitmap(arg2);
	//	    }
	//
	//	    @Override
	//	    public void onLoadingCancelled(String arg0, View arg1) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//	});
	tvNickname.setText(user.getNickname());
	tvAge.setText(user.getAge() + "");

	//	tvDistance.setText(user.getDistance() + "km");

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

	// 必须设置 bounds, 否则不显示
	genderDrawable.setBounds(0, 0, genderDrawable.getMinimumWidth(), genderDrawable.getMinimumHeight());
	tvAge.setCompoundDrawables(genderDrawable, null, null, null);

	return convertView;
    }

    private NearbyUser mCurrentQuestionUser;

    private void showSayhiDialog() {
	if (null == sayhiDialog) {
	    sayhiView = LayoutInflater.from(mContext).inflate(R.layout.dialog_sayhi, null);

	    final EditText etContent = (EditText) sayhiView.findViewById(R.id.et_sayhi);

	    etContent.setText("看了你的资料,我发现你应该就是我一直在寻找的那个人,能认识吗? 期待你的回复!");

	    sayhiDialog = new Dialog(mContext);
	    sayhiDialog.setTitle("向 " + mCurrentSayhiUser.getNickname() + " 打招呼");

	    sayhiView.findViewById(R.id.tv_send_sayhi).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (TextUtils.isEmpty(etContent.getText())) {
			ToastFactory.getToast(mContext, "内容不能为空").show();
			return;
		    }
		    sendSayhi(etContent.getText().toString());
		    sayhiDialog.dismiss();
		}
	    });

	    sayhiView.findViewById(R.id.tv_cancel_send_sayhi).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    sayhiDialog.dismiss();
		}
	    });

	    sayhiDialog.show();

	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    sayhiDialog.addContentView(sayhiView, params);
	}
	if (!sayhiDialog.isShowing()) {

	    sayhiDialog.setTitle("向 " + mCurrentSayhiUser.getNickname() + " 打招呼");

	    sayhiDialog.show();
	}
    }

    private NearbyUser mCurrentSayhiUser;

    private void sendSayhi(String msg) {
	ToastFactory.getToast(mContext, "招呼发送中...").show();
	SayHiParams params = new SayHiParams();
	params.fromUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	params.toUserId = mCurrentSayhiUser.getUserId();
	params.content = msg;
	AppServiceExtendImpl.getInstance().sayhi(params, new OnSayHiResponseListener() {

	    @Override
	    public void onSuccess(int money,String msg) {
		ToastFactory.getToast(mContext, msg).show();
		LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
		loginedUser.setLoveMoney(money);
	    }

	    @Override
	    public void onFailure(String msg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(mContext, msg).show();
	    }
	});
    }

    //    @Override
    //    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    //	// TODO Auto-generated method stub
    //	showToast(data.getStringExtra("question_title"));
    //	return false;
    //    }

    public interface OnQuestionClickListener {
	void onClick(NearbyUser selectedUser);
    }

}
