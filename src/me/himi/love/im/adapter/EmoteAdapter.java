package me.himi.love.im.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.im.entity.FaceText;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class EmoteAdapter extends BaseListAdapter<FaceText> {

    public EmoteAdapter(Context context, List<FaceText> datas) {
	super(context, datas);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (convertView == null) {
	    convertView = mInflater.inflate(R.layout.im_item_face_text, null);

	}
	FaceText faceText = (FaceText) getItem(position);
	String key = faceText.text.substring(1);
	Drawable drawable = mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
	ImageView ivFace = ViewHolder.get(convertView, R.id.v_face_text);

	ivFace.setImageDrawable(drawable);
	return convertView;
    }

}
