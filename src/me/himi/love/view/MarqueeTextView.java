package me.himi.love.view;

import me.himi.love.R;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @ClassName:MarqueeTextView
 * @author sparklee liduanwei_911@163.com
 * @date Mar 24, 2015 4:26:44 PM
 */
public class MarqueeTextView extends TextView {
    private final static int STEP = 12; // 移动步长
    private int offsetX, offsetY;
    private boolean mIsRunning;//
    private float mTextWidth;// 文本宽度
    private String mText;// 当前文本
    private Context mContext;

    private float mWidth; // View最大宽度

    private final Handler mHandler = new Handler() {
	public void handleMessage(android.os.Message msg) {
	    switch (msg.what) {
	    case 0:
		if (mIsRunning) {
		    offsetX -= STEP;
		    if (offsetX < -mTextWidth - 2) {
			// 恢复到最右边,重新开始滚动
			offsetX = (int) mWidth; // 位于父控件的最右边(宽度)的位置
		    }
		    //		    invalidate(); //刷新视图
		    postInvalidate();
		    mHandler.sendEmptyMessageDelayed(0, 100L); // 继续刷新
		}
		break;
	    }
	};
    };

    public MarqueeTextView(Context context) {
	super(context);
	mContext = context;
	init();
    }

    public MarqueeTextView(Context context, AttributeSet attr) {
	super(context, attr);
	mContext = context;
	init();
    }

    public MarqueeTextView(Context context, AttributeSet attr, int defStyle) {
	super(context, attr, defStyle);
	mContext = context;
	init();
    }

    private void init() {
	// TODO Auto-generated method stub
	mIsRunning = true;
	mHandler.sendEmptyMessageDelayed(0, 100L);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	// TODO Auto-generated method stub
	super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// TODO Auto-generated method stub
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	mWidth = getMeasuredWidth();
    }

    @Override
    protected void onAttachedToWindow() {
	// TODO Auto-generated method stub
	mIsRunning = true;
	if (mHandler.hasMessages(0)) {
	    mHandler.removeMessages(0);
	}
	mHandler.sendEmptyMessageDelayed(0, 100L);
	super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
	// TODO Auto-generated method stub
	mIsRunning = false;
	super.onDetachedFromWindow();
    }

    public void setText(String text) {
	// TODO Auto-generated method stub
	mText = text;

	mTextWidth = getPaint().measureText(text.toString());
	offsetX = (int) mWidth;
	offsetY = (int) getPaint().getTextSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub

	//	super.onDraw(canvas);
	if (mText != null && mText.length() != 0) {
	    getPaint().setColor(mContext.getResources().getColor(R.color.text_white));
	    canvas.drawText(mText, offsetX, offsetY, getPaint());
	}

    }

}
