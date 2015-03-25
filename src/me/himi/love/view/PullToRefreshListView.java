package me.himi.love.view;

/**
 * @ClassName:T
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 11:22:03 PM
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import me.himi.love.R;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToRefreshListView extends ListView implements OnScrollListener {

    private static final String TAG = "listview";
    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private final static int NO_MORE = 5; // 没有更多了

    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;

    private LayoutInflater inflater;

    private LinearLayout headView;
    private RelativeLayout footerView;
    private Button mButtonRetryLoadmore;
    private String footerViewText;

    private TextView tipsTextview;
    private TextView lastUpdatedTextView;
    private ImageView arrowImageView;
    private ProgressBar headProgressBar;
    //    private ProgressBar footProgressBar;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecored;

    private int headContentWidth;
    private int headContentHeight;

    private int startY;
    private int firstItemIndex;

    private int state;

    private boolean isBack;

    private OnRefreshListener refreshListener;
    private OnLoadMoreListener loadMoreListener;

    private boolean isRefreshable;

    public PullToRefreshListView(Context context) {
	super(context);
	init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
	super(context, attrs);
	init(context);
    }

    // 
    private static final int LOADING_MORE = 1;
    private static final int LOADED_MORE = 2;
    private int currentLoadMoreState = LOADED_MORE; // 是否已完成加载更多

    private void init(Context context) {
	setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
	inflater = LayoutInflater.from(context);

	headView = (LinearLayout) inflater.inflate(R.layout.mylistview_head, null);

	footerView = (RelativeLayout) inflater.inflate(R.layout.mylistview_footer, null);
	//	footProgressBar = (ProgressBar) footerView.findViewById(R.id.loading_bar);

	mButtonRetryLoadmore = (Button) footerView.findViewById(R.id.button_retry_loadmore);

	// 点击按钮重新加载
	mButtonRetryLoadmore.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (currentLoadMoreState == LOADED_MORE) {
		    currentLoadMoreState = LOADING_MORE;
		    onLoadMore();
		    mButtonRetryLoadmore.setVisibility(View.GONE);
		}
	    }
	});

	arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
	arrowImageView.setMinimumWidth(70);
	arrowImageView.setMinimumHeight(50);
	headProgressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
	tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
	lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

	measureView(headView);
	headContentHeight = headView.getMeasuredHeight();
	headContentWidth = headView.getMeasuredWidth();

	headView.setPadding(0, -1 * headContentHeight, 0, 0);
	headView.invalidate();

	addHeaderView(headView, null, false);

	addFooterView(footerView);

	setOnScrollListener(this);

	animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	animation.setInterpolator(new LinearInterpolator());
	animation.setDuration(250);
	animation.setFillAfter(true);

	reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	reverseAnimation.setInterpolator(new LinearInterpolator());
	reverseAnimation.setDuration(200);
	reverseAnimation.setFillAfter(true);

	state = DONE;
	isRefreshable = false;
    }

    private int lastLoadItemIndex;// 最新更新时的 list 可见底部元素位置

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	firstItemIndex = firstVisibleItem;

	if (firstVisibleItem == 0) {
	    // System.out.println("处于顶部");
	} else if (visibleItemCount + firstVisibleItem >= totalItemCount) {
	    // System.out.println("处于底部");

	    // 没有设置 loadmoreListener 则不显示 
	    if (loadMoreListener == null) {
		//				this.footProgressBar.setVisibility(View.GONE);
		return;
	    }

	    //

	    if (this.lastLoadItemIndex != firstVisibleItem + visibleItemCount) {
		this.lastLoadItemIndex = firstVisibleItem + visibleItemCount;

		if (currentLoadMoreState == LOADED_MORE) { // 判断上次的加载更多是否已完成
		    currentLoadMoreState = LOADING_MORE; // 标记为正在加载更多
		    this.onLoadMore();
		}
	    }

	} else {
	    this.footerView.setVisibility(View.GONE);
	}

    }

    public void onScrollStateChanged(AbsListView listView, int arg1) {
	// Log.i("listView onScrollStateChanged", arg1 + "");
    }

    public boolean onTouchEvent(MotionEvent event) {
	if (isRefreshable) {
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
		if (firstItemIndex == 0 && !isRecored) {
		    isRecored = true;
		    startY = (int) event.getY();
		}
		break;

	    case MotionEvent.ACTION_UP:

		if (state != REFRESHING && state != LOADING) {
		    if (state == DONE) {
			// 什么都不做
		    }
		    if (state == PULL_To_REFRESH) {
			state = DONE;
			changeHeaderViewByState();
		    }
		    if (state == RELEASE_To_REFRESH) {
			state = REFRESHING;
			changeHeaderViewByState();
			onRefresh();
		    }
		}

		isRecored = false;
		isBack = false;

		break;

	    case MotionEvent.ACTION_MOVE:
		int tempY = (int) event.getY();

		if (!isRecored && firstItemIndex == 0) {
		    //                        Log.i(TAG, "在move时候记录下位置");
		    isRecored = true;
		    startY = tempY;
		}

		if (state != REFRESHING && isRecored && state != LOADING) {

		    // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

		    // 可以松手去刷新了
		    if (state == RELEASE_To_REFRESH) {

			setSelection(0);

			// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
			if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
			    state = PULL_To_REFRESH;
			    changeHeaderViewByState();

			    //                                Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
			}
			// 一下子推到顶了
			else if (tempY - startY <= 0) {
			    state = DONE;
			    changeHeaderViewByState();
			    //                                Log.i(TAG, "由松开刷新状态转变到done状态");
			}
			// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
			else {
			    // 不用进行特别的操作，只用更新paddingTop的值就行了
			}
		    }
		    // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
		    if (state == PULL_To_REFRESH) {

			setSelection(0);

			// 下拉到可以进入RELEASE_TO_REFRESH的状态
			if ((tempY - startY) / RATIO >= headContentHeight) {
			    state = RELEASE_To_REFRESH;
			    isBack = true;
			    changeHeaderViewByState();

			    //                                Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
			}
			// 上推到顶了
			else if (tempY - startY <= 0) {
			    state = DONE;
			    changeHeaderViewByState();
			    //                                Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
			}
		    }

		    // done状态下
		    if (state == DONE) {
			if (tempY - startY > 0) {
			    state = PULL_To_REFRESH;
			    changeHeaderViewByState();
			}
		    }

		    // 更新headView的size
		    if (state == PULL_To_REFRESH) {
			headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);

		    }

		    // 更新headView的paddingTop
		    if (state == RELEASE_To_REFRESH) {
			headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
		    }
		}
		break;
	    }
	}

	return super.onTouchEvent(event);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
	switch (state) {
	case RELEASE_To_REFRESH:
	    arrowImageView.setVisibility(View.VISIBLE);
	    headProgressBar.setVisibility(View.GONE);
	    tipsTextview.setVisibility(View.VISIBLE);
	    lastUpdatedTextView.setVisibility(View.VISIBLE);

	    arrowImageView.clearAnimation();
	    arrowImageView.startAnimation(animation);
	    //	    tipsTextview.setText("放开可以刷新");
	    break;
	case PULL_To_REFRESH:
	    headProgressBar.setVisibility(View.GONE);
	    tipsTextview.setVisibility(View.VISIBLE);
	    lastUpdatedTextView.setVisibility(View.VISIBLE);
	    arrowImageView.clearAnimation();
	    arrowImageView.setVisibility(View.VISIBLE);
	    // 是由RELEASE_To_REFRESH状态转变来的
	    if (isBack) {
		isBack = false;
		arrowImageView.clearAnimation();
		arrowImageView.startAnimation(reverseAnimation);
		//		tipsTextview.setText("下拉可以刷新");
	    } else {
		//		tipsTextview.setText("下拉可以刷新");
	    }
	    break;

	case REFRESHING:
	    headView.setPadding(0, 0, 0, 0);
	    headProgressBar.setVisibility(View.VISIBLE);
	    arrowImageView.clearAnimation();
	    arrowImageView.setVisibility(View.GONE);
	    //	    tipsTextview.setText("正在刷新...");
	    lastUpdatedTextView.setVisibility(View.VISIBLE);
	    break;
	case DONE:
	    headView.setPadding(0, -1 * headContentHeight, 0, 0);

	    headProgressBar.setVisibility(View.GONE);
	    arrowImageView.clearAnimation();
	    arrowImageView.setImageResource(R.drawable.arrow);
	    //	    tipsTextview.setText("下拉可以刷新");
	    lastUpdatedTextView.setVisibility(View.VISIBLE);

	    //                Log.i(TAG, "当前状态，done");
	    break;
	}
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
	this.refreshListener = refreshListener;
	isRefreshable = true;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadmoreListener) {
	this.loadMoreListener = loadmoreListener;
	isRefreshable = true;
    }

    public interface OnRefreshListener {
	public void onRefresh();

    }

    public interface OnLoadMoreListener {
	public void onLoadMore();
    }

    public void onRefreshComplete() {

	state = DONE;
	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
	String time = sdf.format(new Date());

	lastUpdatedTextView.setText(time);
	changeHeaderViewByState();

	currentLoadMoreState = LOADED_MORE;
    }

    public void onLoadMoreComplete() {
	// 标记为已完成加载更多
	currentLoadMoreState = LOADED_MORE;
    }

    private void onRefresh() {
	if (refreshListener != null) {
	    refreshListener.onRefresh();
	}
    }

    private void onLoadMore() {
	if (loadMoreListener != null) {
	    loadMoreListener.onLoadMore();
	}
    }

    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
    private void measureView(View child) {
	ViewGroup.LayoutParams p = child.getLayoutParams();
	if (p == null) {
	    p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
	int lpHeight = p.height;
	int childHeightSpec;
	if (lpHeight > 0) {
	    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
	} else {
	    childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	}
	child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {

	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

	String time = sdf.format(new Date());
	lastUpdatedTextView.setText(time);
	super.setAdapter(adapter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	// // TODO Auto-generated method stub
	// super.onDraw(canvas);
    }

    public void setRetryLoadMoreButtonVisible(boolean visible) {
	this.mButtonRetryLoadmore.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public String getFooterViewText() {
	return footerViewText;
    }

    public void setFooterViewText(String footerViewText) {
	this.footerViewText = footerViewText;
	((TextView) this.footerView.findViewById(R.id.ask_for_more)).setText(this.footerViewText);
    }
}
