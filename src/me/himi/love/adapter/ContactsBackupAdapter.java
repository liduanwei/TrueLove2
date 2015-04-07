package me.himi.love.adapter;

import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.OnVoteArticleLoveResponseListener;
import me.himi.love.IAppServiceExtend.VoteArticleLovePostParams;
import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.Article;
import me.himi.love.ui.ContactsBackupHistoryActivity.ContactBackup;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.ZoombleImageActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.Share;
import me.himi.love.view.EmojiTextView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ContactsBackupAdapter extends BaseListAdapter<ContactBackup> {

    public ContactsBackupAdapter(Context context, List<ContactBackup> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_backup_history_item, null);
	}

	int backColor = Color.rgb(255, 255, 255);
	if (position % 2 == 0) {
	    backColor = Color.rgb(250, 250, 250);
	} else {
	    backColor = Color.rgb(255, 255, 255);
	}

	convertView.setBackgroundColor(backColor);

	final ContactBackup backup = this.list.get(position);

	TextView tvSize = ViewHolder.get(convertView, R.id.tv_backup_size);
	TextView tvTime = ViewHolder.get(convertView, R.id.tv_backup_time);
	tvSize.setText(backup.contactsSize + " 联系人");
	tvTime.setText("备份时间: " + ActivityUtil.getTimeStr("yyyy/MM/dd HH:mm:ss", backup.addtime));
	return convertView;
    }

}
