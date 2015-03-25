package me.himi.love.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 解决外部嵌套ScrollView时不显示(没有高度)的问题
 * @ClassName:CustomViewPager
 * @author sparklee liduanwei_911@163.com
 * @date Nov 29, 2014 12:27:58 PM
 */
public class CustomViewPager extends ViewPager {
    public CustomViewPager(Context context) {
	super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	int height = 0;
	for (int i = 0; i < getChildCount(); i++) {
	    View child = getChildAt(i);
	    child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    int h = child.getMeasuredHeight();
	    if (h > height)
		height = h;
	}
	heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
