package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.MyQuestion;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @ClassName:PrivateMessageAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */
public class MyCreatedQuestionsAdapter extends BaseListAdapter<MyQuestion> {
    private boolean isEditMode;

    public MyCreatedQuestionsAdapter(Context context, List<MyQuestion> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.myquestion_item, null);
	}

	TextView tvTitle = ViewHolder.get(convertView, R.id.tv_myquestion_title);
	TextView tvCreatedTime = ViewHolder.get(convertView, R.id.tv_myquestion_createtime);

	TextView tvEdit = ViewHolder.get(convertView, R.id.tv_myquestion_edit);
	TextView tvDelete = ViewHolder.get(convertView, R.id.tv_myquestion_delete);

	MyQuestion question = list.get(position);
	tvTitle.setText(question.getTitle());
	tvCreatedTime.setText(question.getCreateTimeStr());

	if (isEditMode) {
	    tvEdit.setVisibility(View.VISIBLE);
	    tvDelete.setVisibility(View.VISIBLE);
	} else {
	    tvEdit.setVisibility(View.GONE);
	    tvDelete.setVisibility(View.GONE);
	}

	return convertView;
    }

    public boolean isEditMode() {
	return isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
	this.isEditMode = isEditMode;
    }

}
