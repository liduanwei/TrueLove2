package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @ClassName:ColorSettingsActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ColorSettingsActivity extends BaseActivity {

    SharedPreferences mSp = null; // 默认颜色方案 
    int[] defaultColos = {};

    static public final String KEY_COLORS = "colors";

    boolean isEditing;// 是否处于编辑状态

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_color_settings);

	mSp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	defaultColos = new int[] { getResources().getColor(R.color.bar), getResources().getColor(R.color.bar2), getResources().getColor(R.color.bar3) };
	init();
    }

    private void init() {

	readAndSetTopBackgroundColor();

	// TODO Auto-generated method stub
	TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopTitle.setText("[配置导航栏颜色");

	final TextView tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	//	tvTopAction.setVisibility(View.GONE);
	tvTopAction.setText("编辑");

	final LinearLayout colorsLayout = (LinearLayout) findViewById(R.id.layout_colors);

	tvTopAction.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (isEditing) {
		    isEditing = false;
		    tvTopAction.setText("编辑");
		    // 恢复隐藏状态
		    for (int i = 0, n = colorItems.size(); i < n; ++i) {
			View btnDelete = colorItems.get(i).view.findViewById(R.id.tv_color_item_delete);
			btnDelete.setVisibility(View.GONE);
		    }
		    return;
		}
		isEditing = true;

		tvTopAction.setText("完成");

		// TODO Auto-generated method stub
		//Share.share(ColorSettingsActivity.this, "我正在使用恋恋征婚交友APP,这是我的导航栏颜色配置方案");
		// 绘制所有删除按钮
		for (int i = 0, n = colorItems.size(); i < n; ++i) {
		    final View colorItem = colorItems.get(i).view;
		    View btnDelete = colorItem.findViewById(R.id.tv_color_item_delete);
		    btnDelete.setVisibility(View.VISIBLE);
		}
	    }
	});

	// 读取已保存的颜色方案
	loadColors();//

	//alpha, red,green,blue 调节控件父layout
	final LinearLayout seekbarsLayout = (LinearLayout) findViewById(R.id.layout_seekbars);
	seekbarsLayout.setVisibility(View.GONE);

	// 添加颜色方案
	findViewById(R.id.tv_addcolor).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 可视
		seekbarsLayout.setVisibility(View.VISIBLE);
	    }
	});

	// 读取已保存的自定义颜色方案
	// todo

	final SeekBar sbAlpha = getViewById(R.id.sb_alpha);
	final SeekBar sbRed = getViewById(R.id.sb_red);
	final SeekBar sbGreen = getViewById(R.id.sb_green);
	final SeekBar sbBlue = getViewById(R.id.sb_blue);

	int color = readTopBackgroundColor();
	sbAlpha.setProgress(color >> 24 & 0xff);
	sbRed.setProgress(color >> 16 & 0xff);
	sbGreen.setProgress(color >> 8 & 0xff);
	sbBlue.setProgress(color & 0xff);

	OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		// 0xffffffff
		// 111111111 1111111 11111111 11111111
		int color = (sbAlpha.getProgress() << 24) + (sbRed.getProgress() << 16) + (sbGreen.getProgress() << 8) + (sbBlue.getProgress());

		//		System.out.println(Integer.toHexString(color));
		findViewById(R.id.layout_nav_top).setBackgroundColor(color);
	    }
	};

	sbAlpha.setOnSeekBarChangeListener(listener);
	sbRed.setOnSeekBarChangeListener(listener);
	sbGreen.setOnSeekBarChangeListener(listener);
	sbBlue.setOnSeekBarChangeListener(listener);

	// 取消添加
	seekbarsLayout.findViewById(R.id.tv_savecolor_cancle).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		seekbarsLayout.setVisibility(View.GONE);
		findViewById(R.id.layout_nav_top).setBackgroundColor(readTopBackgroundColor());
	    }
	});

	// 确认添加
	seekbarsLayout.findViewById(R.id.tv_savecolor).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		seekbarsLayout.setVisibility(View.GONE);
		int color = (sbAlpha.getProgress() << 24) + (sbRed.getProgress() << 16) + (sbGreen.getProgress() << 8) + (sbBlue.getProgress());
		saveTopBackgroundColor(color);
		//保存所有颜色方案
		saveColors(color);
		// 
		createAndAddView(color);

		findViewById(R.id.layout_nav_top).setBackgroundColor(color);
	    }
	});

    }

    private void saveColors(List<WithColor> colorItems) {
	// TODO Auto-generated method stub
	// 先写入默认配置

	String colors = "";
	for (int i = 0, n = colorItems.size(); i < n; ++i) {
	    WithColor item = colorItems.get(i);
	    if (item.view.getVisibility() != View.VISIBLE) {
		continue;
	    }
	    colors += item.color;
	    if (i >= n - 1) {
		break;
	    }
	    colors += ",";
	}
	if (colors.trim().length() == 0) {
	    mSp.edit().putString(KEY_COLORS, defaultColos[0] + "," + defaultColos[1] + "," + defaultColos[2]).commit();
	} else {
	    mSp.edit().putString(KEY_COLORS, colors).commit();
	}
    }

    // 配色Items
    private List<WithColor> colorItems = new ArrayList<WithColor>();

    /**
     * 读取颜色方案
     */
    private void loadColors() {
	String colorsStr = mSp.getString(KEY_COLORS, defaultColos[0] + "," + defaultColos[1] + "," + defaultColos[2]);
	String[] colors = colorsStr.split(",");
	// 颜色方案 

	colorItems.clear();

	boolean isEmpty = true;
	for (int i = 0, n = colors.length; i < n; ++i) {
	    try {
		// 数据
		View v = createAndAddView(Integer.parseInt(colors[i]));
		isEmpty = false;
	    } catch (Throwable th) {
		break;
	    }
	}

	if (isEmpty) {
	    mSp.edit().putString(KEY_COLORS, defaultColos[0] + "," + defaultColos[1] + "," + defaultColos[2]).commit();
	    loadColors();
	}
    }

    private View createAndAddView(final int color) {

	// TODO Auto-generated method stub
	// 颜色方案 父layout
	LinearLayout colorsLayout = (LinearLayout) findViewById(R.id.layout_colors);

	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

	final View itemView = getLayoutInflater().inflate(R.layout.layout_top_color_item, null);
	itemView.setBackgroundColor(color);

	itemView.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		saveTopBackgroundColor(color);

		findViewById(R.id.layout_nav_top).setBackgroundColor(color);

		//		Drawable drawable = v.getBackground();
		//		if (drawable instanceof ColorDrawable) {
		//		    ColorDrawable colorDrawable = (ColorDrawable) drawable.mutate();
		//		    int color = colorDrawable.getColor();
		//		    saveTopBackgroundColor(color);
		//		}

	    }
	});

	// 默认隐藏删除按钮
	View deleteView = itemView.findViewById(R.id.tv_color_item_delete);

	if (isEditing) {
	    deleteView.setVisibility(View.VISIBLE);
	} else {
	    deleteView.setVisibility(View.GONE);
	}

	// 删除按钮
	deleteView.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		itemView.setVisibility(View.GONE);
		saveColors(colorItems);
	    }
	});

	colorsLayout.addView(itemView, params);

	// 添加view
	addToList(itemView, color);
	return itemView;
    }

    /**
     * 添加到list
     * 
     * @param v
     */
    private void addToList(View v, int color) {
	// TODO Auto-generated method stub
	colorItems.add(new WithColor(v, color));
    }

    /**
     * 
     * @param color
     */
    private void saveColors(int color) {
	String colorsStr = mSp.getString(KEY_COLORS, defaultColos[0] + "," + defaultColos[1] + "," + defaultColos[2]);
	colorsStr += "," + color;
	mSp.edit().putString(KEY_COLORS, colorsStr).commit();
    }

    static class WithColor {
	public View view;
	public int color;

	public WithColor(View v, int color) {
	    this.view = v;
	    this.color = color;
	}
    }
}
