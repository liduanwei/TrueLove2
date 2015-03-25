package me.himi.love.view;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.util.ActivityUtil;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName:ListMenuDialog
 * @author sparklee liduanwei_911@163.com
 * @date Nov 17, 2014 12:08:18 AM
 */
public class ListMenuDialog extends Dialog {

    private List<MenuItem> data;

    private LinearLayout mContainerView;

    private boolean isMulitSelect = false;

    private OnSubmitListener onSubmitListener;

    public ListMenuDialog(Context context, boolean isMulitSelect) {
	super(context);
	this.isMulitSelect = isMulitSelect;
	this.data = new ArrayList<MenuItem>();
	initViews();
    }

    @Override
    public void setTitle(CharSequence title) {
	// TODO Auto-generated method stub
	this.tvTitle.setText(title);
    }

    public void show() {

	super.show();
	TextView tvSubmit = (TextView) mLayoutRoot.findViewById(R.id.tv_submit);
	// 
	tvSubmit.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		dismiss();
		if (onSubmitListener != null) {

		    List<MenuItem> checkedItems = new ArrayList<MenuItem>();

		    for (MenuItem item : data) {
			// 
			if (item.isChecked()) {
			    checkedItems.add(item);
			}
		    }
		    onSubmitListener.onSubmit(checkedItems, etInput.getText().toString());
		}
	    }
	});

	ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
	//	for (int i = 0, n = data.size(); i < n; ++i) {
	//	    params.height += 150;
	//	}
	params.height = (int) (ActivityUtil.getScreenSize()[1] / 1.8f);
	mLayoutRoot.setLayoutParams(params);
	addContentView(mLayoutRoot, params);

    }

    private boolean hasChecked; // 用于记录用户设置的是否为单选模式下是否已添加过选中的

    public ListMenuDialog addMenuItem(MenuItem menuItem) {

	if (!isMulitSelect) {
	    if (menuItem.isChecked) {
		if (!hasChecked) {
		    menuItem.setChecked(true);
		    lastCheckItem = menuItem;
		    etInput.setText(menuItem.name);
		    hasChecked = true;
		} else {
		    menuItem.setChecked(false);
		}
	    }
	} else {
	    menuItem.setChecked(menuItem.isChecked);
	}

	this.data.add(menuItem);

	menuItem.onClickListener = new OnMenuItemClickListener() {

	    @Override
	    public void onClick(MenuItem item) {
		// TODO Auto-generated method stub
		if (!isMulitSelect) {
		    if (lastCheckItem != item) {
			if (lastCheckItem != null) {
			    lastCheckItem.setChecked(false);
			}
			lastCheckItem = item;
			if (item.isChecked) {
			    etInput.setText(item.name);
			}
		    } else {
			item.setChecked(true);
			etInput.setText(item.name);
		    }
		}
	    }
	};
	if (!isMulitSelect) {
	    if (menuItem.isChecked) {
		lastCheckItem = menuItem;
	    }
	}

	mContainerView.addView(menuItem.view);
	return this;
    }

    public void setChecked(String name) {
	for (MenuItem menuItem : data) {
	    if (menuItem.getName().equals(name)) {
		if (!isMulitSelect) {
		    if (!hasChecked) {
			menuItem.setChecked(true);
			lastCheckItem = menuItem;
			hasChecked = true;
		    } else {
			menuItem.setChecked(false);
		    }
		} else {
		    menuItem.setChecked(true);
		}
		break;
	    }
	}
    }

    /**
     * 设定初始输入框中的内容
     * @param content
     */
    public void setInputContent(String content) {

	this.etInput.setText(content);
    }

    private RelativeLayout mLayoutRoot;

    private EditText etInput;

    private TextView tvTitle;

    private void initViews() {

	// 去掉标题部分
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	this.mLayoutRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.listmenudialog_main, null);
	this.mContainerView = (LinearLayout) this.mLayoutRoot.findViewById(R.id.ll_main_content);

	this.etInput = ((EditText) mLayoutRoot.findViewById(R.id.et_listmenu_input));

	this.tvTitle = (TextView) mLayoutRoot.findViewById(R.id.tv_title);

	//	LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	//	params.height = (int) (ActivityUtil.getScreenSize()[1] / 1.5f);
	//	addContentView(mLayoutRoot, params);

    }

    public interface OnMenuItemClickListener {
	void onClick(MenuItem item);
    }

    public class MenuItem {
	private View view;
	private CheckBox checkBox;
	private OnMenuItemClickListener onClickListener;

	public MenuItem(String name) {
	    this.name = name;
	    this.view = getLayoutInflater().inflate(R.layout.listmenu_item, null);
	    ((TextView) view.findViewById(R.id.tv_listmen_label_name)).setText(name);
	    checkBox = (CheckBox) view.findViewById(R.id.cb_listmenu_select);

	    this.view.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    setChecked(!checkBox.isChecked());
		    if (onClickListener != null) {
			onClickListener.onClick(MenuItem.this);
		    }
		}
	    });

	}

	private String name;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	private boolean isChecked;

	public boolean isChecked() {
	    return isChecked;
	}

	public void setChecked(boolean isChecked) {
	    this.isChecked = isChecked;
	    this.checkBox.setChecked(isChecked);

	}

	public int getHeight() {
	    return this.view.getHeight();
	}

    }

    private MenuItem lastCheckItem;

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
	this.onSubmitListener = onSubmitListener;
    }

    public static interface OnSubmitListener {
	void onSubmit(List<MenuItem> checkedItems, String input);
    }

}
