package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.OnPostNearbyUsersResponseListener;
import me.himi.love.IAppServiceExtend.OnPostQuestionResponseListener;
import me.himi.love.IAppServiceExtend.PostNearbyUsersParams;
import me.himi.love.IAppServiceExtend.PostQuestionParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.UserNearbyAdapter;
import me.himi.love.adapter.UserNearbyAdapter.OnQuestionClickListener;
import me.himi.love.entity.MyQuestion;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.loader.IRecommendUserLoader;
import me.himi.love.entity.loader.impl.NearbyUserLoaderImpl;
import me.himi.love.ui.MyCreatedQuestionsActivity;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.ui.sound.SoundPlayer;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUtils;
import me.himi.love.view.ListMenuDialog;
import me.himi.love.view.ListMenuDialog.MenuItem;
import me.himi.love.view.SelectProvinceCityPopupWindow;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class NearbyFragment extends BaseFragment implements OnItemClickListener, View.OnClickListener {
    me.himi.love.view.list.XListView mListView;
    UserNearbyAdapter mAdapter;
    List<NearbyUser> mUsers = new ArrayList<NearbyUser>();

    View mContainerView;

    IRecommendUserLoader recommendUserLoader = new NearbyUserLoaderImpl();

    View mLayoutSearch;
    ViewPager mViewPagerSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.e(getClass().getSimpleName(), "->onCreateView");
	mContainerView = inflater.inflate(R.layout.fragment_nearby, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	//	init();
    }

    private int pageNumber = 1;

    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {

	@Override
	public void onClick(View v) {
	    showSearch();
	}
    };

    TextView tvTopAction;

    private void init() {
	tvTopAction = (TextView) getActivity().findViewById(R.id.tv_top_action);

	// 搜索过滤的结果 临时
	final List<NearbyUser> tmpUsers = new ArrayList<NearbyUser>();

	final EditText etSearchFilter = (EditText) mContainerView.findViewById(R.id.et_nearby_search);
	etSearchFilter.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		final String input = etSearchFilter.getText().toString().trim();
		if (input.length() == 0) {
		    mAdapter.setList(mUsers);
		    // 不起作用
		    //ActivityUtil.closeSoftInput(getActivity(), etSearchFilter);
		    //		    etSearchFilter.clearFocus();
		    //		    ActivityUtil.hideSoftInput(getActivity());
		    return;
		}
		tmpUsers.clear();

		List<NearbyUser> users = mUsers;
		for (NearbyUser u : users) {
		    if (u.getNickname().toLowerCase().contains(input.toLowerCase())) {
			tmpUsers.add(u);
		    }
		}
		mAdapter.setList(tmpUsers);
		//		Collections.sort(users, new Comparator<NearbyUser>() {
		//
		//		    @Override
		//		    public int compare(NearbyUser lhs, NearbyUser rhs) {
		//			// TODO Auto-generated method stub
		//			return 0;
		//		    }
		//		});
	    }
	});

	// 提示暂无附近的人
	mContainerView.findViewById(R.id.tv_empty_peopel_nearby_tips).setVisibility(View.GONE);

	mContainerView.findViewById(R.id.et_nearby_search).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		mContainerView.findViewById(R.id.et_nearby_search).requestFocus();
	    }
	});

	this.mListView = (me.himi.love.view.list.XListView) mContainerView.findViewById(R.id.listview_nearby);

	this.mAdapter = new UserNearbyAdapter(this.getActivity(), mUsers);

	this.mAdapter.setOnQuestionClickListener(new OnQuestionClickListener() {

	    @Override
	    public void onClick(NearbyUser selectedUser) {
		// TODO Auto-generated method stub
		showQuestionDialog(selectedUser);
	    }
	});

	this.mListView.setAdapter(this.mAdapter);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	this.mListView.setOnItemClickListener(this);

	// 首先允许加载更多
	mListView.setPullLoadEnable(true);
	// 允许下拉
	mListView.setPullRefreshEnable(true);
	// 设置监听器
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadRecommendUsers();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		// 如果当前处于刷新状态则不加载更多
		loadRecommendUsers();
	    }
	});
	mListView.pullRefreshing();
	mListView.setDividerHeight(0);

	// 长按显示菜单
	mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

	    @Override
	    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		NearbyUser user = mAdapter.getList().get(position - 1);
		showQuestionDialog(user);
		return false;
	    }
	});

	//	loadRecommendUsers();
	// 优先从缓存加载
	List<NearbyUser> users = CacheUtils.loadFromCache(cacheUsersPath);
	if (users != null) {
	    mAdapter.setList(users);
	} else {
	    loadRecommendUsers();
	}
	// 广告初始化
	initAds();

	initSearchView();

    }

    TextView mTvSearchHomeplace;
    TextView mTvSearchAddress;
    TextView mTvSearchAge;
    TextView mTvSearchSalary;
    TextView mTvSearchHouse;
    TextView mTvSearchSatus;
    TextView mTvSearchHeight;
    TextView mTvSearchWeight;
    TextView mTvSearchStar;

    List<View> searchItemViews = new ArrayList<View>();

    int mCurrentIndex;

    /**
     * 初始化搜索 view
     */
    private void initSearchView() {
	// TODO Auto-generated method stub
	//	View viewGender = getActivity().getLayoutInflater().inflate(R.layout.layout_users_search_gender, null);
	//	View viewHomeplace = getActivity().getLayoutInflater().inflate(R.layout.layout_users_search_homeplace, null);
	//	searchItemViews.add(viewGender);
	//	searchItemViews.add(viewHomeplace);

	// 筛选view
	mLayoutSearch = mContainerView.findViewById(R.id.layout_search);
	//	mViewPagerSearch = (ViewPager) mLayoutSearch.findViewById(R.id.viewpager);
	//	mViewPagerSearch.setAdapter(new PagerAdapter() {
	//
	//	    @Override
	//	    public void destroyItem(ViewGroup container, int position, Object object) {
	//		// TODO Auto-generated method stub
	//		container.removeView(searchItemViews.get(position));
	//	    }
	//
	//	    @Override
	//	    public Object instantiateItem(ViewGroup container, int position) {
	//		// TODO Auto-generated method stub
	//		View view = searchItemViews.get(position);
	//		container.addView(view);
	//		return view;
	//	    }
	//
	//	    @Override
	//	    public boolean isViewFromObject(View arg0, Object arg1) {
	//		// TODO Auto-generated method stub
	//		return arg0 == arg1;
	//	    }
	//
	//	    @Override
	//	    public int getCount() {
	//		// TODO Auto-generated method stub
	//		return searchItemViews.size();
	//	    }
	//	});
	//	mViewPagerSearch.setOnPageChangeListener(new OnPageChangeListener() {
	//	    
	//	    @Override
	//	    public void onPageSelected(int arg0) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	    
	//	    @Override
	//	    public void onPageScrolled(int arg0, float arg1, int arg2) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	    
	//	    @Override
	//	    public void onPageScrollStateChanged(int arg0) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	});

	mLayoutSearch.setVisibility(View.GONE); // 默认不显示

	// 家乡
	mTvSearchHomeplace = (TextView) mLayoutSearch.findViewById(R.id.tv_homeplace);
	// 现居地
	mTvSearchAddress = (TextView) mLayoutSearch.findViewById(R.id.tv_address);
	// 年龄
	mTvSearchAge = (TextView) mLayoutSearch.findViewById(R.id.tv_age);
	// 月薪
	mTvSearchSalary = (TextView) mLayoutSearch.findViewById(R.id.tv_salary);
	// 房
	mTvSearchHouse = (TextView) mLayoutSearch.findViewById(R.id.tv_house);
	// 情感
	mTvSearchSatus = (TextView) mLayoutSearch.findViewById(R.id.tv_marstatus);
	// 身高
	mTvSearchHeight = (TextView) mLayoutSearch.findViewById(R.id.tv_height);
	// 体重
	mTvSearchWeight = (TextView) mLayoutSearch.findViewById(R.id.tv_weight);
	// 星座
	mTvSearchStar = (TextView) mLayoutSearch.findViewById(R.id.tv_star);

	mLayoutSearch.findViewById(R.id.tv_search_confirm).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

		mLayoutSearch.setVisibility(View.GONE);
		// 提交搜索

		AnimationSet as = new AnimationSet(true);
		TranslateAnimation translateAnmation = new TranslateAnimation(0, 0, 100, -1000);
		translateAnmation.setFillAfter(true);
		translateAnmation.setDuration(200L);
		as.addAnimation(translateAnmation);
		mLayoutSearch.startAnimation(as);
	    }
	});
	mLayoutSearch.findViewById(R.id.tv_search_cancle).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		mLayoutSearch.setVisibility(View.GONE);
		AnimationSet as = new AnimationSet(true);
		TranslateAnimation translateAnmation = new TranslateAnimation(0, 0, 100, -1000);
		translateAnmation.setFillAfter(true);
		translateAnmation.setDuration(200L);
		as.addAnimation(translateAnmation);
		mLayoutSearch.startAnimation(as);
	    }
	});

	// 家乡
	mLayoutSearch.findViewById(R.id.layout_search_homeplace).setOnClickListener(this);
	// 现居地
	mLayoutSearch.findViewById(R.id.layout_search_address).setOnClickListener(this);
	// 年龄
	mLayoutSearch.findViewById(R.id.layout_search_age).setOnClickListener(this);
	// 情感状态
	mLayoutSearch.findViewById(R.id.layout_search_status).setOnClickListener(this);
	// 月薪
	mLayoutSearch.findViewById(R.id.layout_search_salary).setOnClickListener(this);
	// 房
	mLayoutSearch.findViewById(R.id.layout_search_house).setOnClickListener(this);
	// 情感
	mLayoutSearch.findViewById(R.id.layout_search_status).setOnClickListener(this);
	// 身高
	mLayoutSearch.findViewById(R.id.layout_search_height).setOnClickListener(this);
	// 体重
	mLayoutSearch.findViewById(R.id.layout_search_weight).setOnClickListener(this);
	// 星座
	mLayoutSearch.findViewById(R.id.layout_search_star).setOnClickListener(this);

    }

    private void initAds() {
	initDomobAd();
	initBaiduAd();
    }

    /**
     * 广告
     */
    private void initBaiduAd() {
	// TODO Auto-generated method stub
	//
	//	IconsAd iconsAd = new IconsAd(this.getActivity());
	//	iconsAd.loadAd(getActivity());
    }

    /**
     * 广告 初始化
     */
    private void initDomobAd() {
	cn.domob.android.ads.AdView adview = new cn.domob.android.ads.AdView(MyApplication.getInstance().getTopActivity(), "56OJzoHYuN5N9Wvxuc", "16TLmU5aApZM2NUOnL9qT5Ii");
	// // 广告 container
	RelativeLayout adContainer = (RelativeLayout) mContainerView.findViewById(R.id.layout_domob_adView);
	adview.setGravity(Gravity.CENTER);
	//	adview.setAdEventListener(null);
	adContainer.addView(adview);
    }

    // 使用本地缓存
    private final static String cacheUsersPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/users";

    private boolean isRefreshing;// 刷新中...

    private void loadRecommendUsers() {

	if (isRefreshing) { // 防止数据未加载页面号却增加了
	    return;
	}
	isRefreshing = true; // 刷新中...

	if (!ActivityUtil.hasNetwork(getActivity())) {
	    ActivityUtil.show(getActivity(), "请开启网络");
	    //	    mListView.onRefreshComplete();
	    return;
	}

	//	Map<String, String> nameAndValues = new HashMap<String, String>();
	//	nameAndValues.put("page", pageNumber + "");
	//	nameAndValues.put("page_size", 20 + "");
	//
	//	RequestParams params = new RequestParams(nameAndValues);
	//	HttpUtil.post(Constants.URL_DESTINY_USER, params, new AsyncHttpResponseHandler() {
	//
	//	    @Override
	//	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	//
	//		String response = new String(arg2);
	//
	//		List<RecommendUser> users = recommendUserLoader.load(response);
	//
	//		mListView.stopRefresh();
	//		mListView.stopLoadMore();
	//		if (users.size() != 0) {
	//		    if (pageNumber == 1) { // 当前是首页则表示刷新
	//			mUserRecommendAdapter.getList().clear();
	//		    }
	//
	//		    mUserRecommendAdapter.addAll(users);
	//		} else {
	//		    // 没有数据或没有更多数据了
	//		    if (pageNumber == 1) {
	//			ActivityUtil.show(MyApplication.getInstance().getApplicationContext(), "暂无数据");
	//		    } else {
	//			ActivityUtil.show(MyApplication.getInstance().getApplicationContext(), "暂无更多");
	//		    }
	//		}
	//
	//		pageNumber++;
	//	    }
	//
	//	    @Override
	//	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
	//		ActivityUtil.show(getActivity(), "网络超时");
	//		mListView.stopRefresh();
	//		mListView.stopLoadMore();
	//	    }
	//	});

	PostNearbyUsersParams postParams = new PostNearbyUsersParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;

	postParams.longtitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();

	AppServiceExtendImpl.getInstance().loadNearbyUsers(postParams, new OnPostNearbyUsersResponseListener() {

	    @Override
	    public void onSuccess(List<NearbyUser> users) {
		// TODO Auto-generated method stub

		if (users.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();

			// 刷新加载到数据时播放音乐
			SoundPlayer.getInstance(getActivity()).playOk();
		    }
		    mAdapter.addAll(users);
		    pageNumber++;

		    // 缓存到本地,下次启动首先从缓存加载
		    CacheUtils.cacheToLocal(mAdapter.getList(), cacheUsersPath);

		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无数据");
		}

		// 刷新完成
		isRefreshing = false;

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		isRefreshing = false;

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		showToast(errorMsg);
	    }
	});

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
	super.onHiddenChanged(hidden);
	if (!hidden) {
	    if (tvTopAction != null) {
		tvTopAction.setVisibility(View.VISIBLE);
		tvTopAction.setText("筛选");
		tvTopAction.setOnClickListener(searchOnClickListener);
	    }
	}
    }

    Dialog questionDialog;
    View questionView;

    private void showQuestionDialog(final NearbyUser user) {
	if (null == questionDialog) {
	    questionDialog = new Dialog(getActivity());
	}

	if (questionView == null) {
	    questionView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_question, null);
	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    questionDialog.addContentView(questionView, params);
	}

	final EditText etQuestionTitle = (EditText) questionView.findViewById(R.id.et_question_title);
	final EditText etQuestionOption1 = (EditText) questionView.findViewById(R.id.et_question_option1);
	final EditText etQuestionOption2 = (EditText) questionView.findViewById(R.id.et_question_option2);
	final EditText etQuestionOption3 = (EditText) questionView.findViewById(R.id.et_question_option3);
	final EditText etQuestionOption4 = (EditText) questionView.findViewById(R.id.et_question_option4);

	// 发送
	questionView.findViewById(R.id.tv_send_question).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (TextUtils.isEmpty(etQuestionTitle.getText())) {
		    showToast("标题不能为空");
		    etQuestionTitle.requestFocus();
		    return;
		}
		if (TextUtils.isEmpty(etQuestionOption1.getText())) {
		    showToast("选项1不能为空");
		    return;
		}

		if (TextUtils.isEmpty(etQuestionOption2.getText())) {
		    showToast("选项2不能为空");
		    return;
		}

		MyQuestion myQuestion = new MyQuestion();
		myQuestion.setTitle(etQuestionTitle.getText().toString());
		List<String> options = new ArrayList<String>();
		options.add(etQuestionOption1.getText().toString());
		options.add(etQuestionOption2.getText().toString());
		options.add(etQuestionOption3.getText().toString());
		options.add(etQuestionOption4.getText().toString());
		myQuestion.setOptions(options);
		myQuestion.setCreateTime((int) (System.currentTimeMillis() / 1000L));

		// 判断是否为选取的内容
		//		DBHelper dbHelper = DBHelper.getInstance(getActivity(), DBHelper.DB_NAME, null, 1);
		//		dbHelper.insertMyQuestion(myQuestion);

		sendMyQuestion(user, myQuestion);
	    }
	});

	// 选取我的问题
	questionView.findViewById(R.id.tv_select_question).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), MyCreatedQuestionsActivity.class);
		startActivityForResult(intent, 1);
	    }
	});

	// 取消
	questionView.findViewById(R.id.tv_cancel_send_question).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		questionDialog.dismiss();
	    }
	});

	if (!questionDialog.isShowing()) {
	    questionDialog.setTitle("向 " + user.getNickname() + " 提问题");
	    questionDialog.show();
	}
    }

    /**
     * 发送我的问题
     * @param user
     * @param myQuestion
     */
    private void sendMyQuestion(NearbyUser user, MyQuestion myQuestion) {
	PostQuestionParams postParams = new PostQuestionParams();
	postParams.fromUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	postParams.toUserId = user.getUserId();
	postParams.title = myQuestion.getTitle();
	postParams.option1 = myQuestion.getOptions().get(0);
	postParams.option2 = myQuestion.getOptions().get(1);
	postParams.option3 = myQuestion.getOptions().get(2);
	postParams.option4 = myQuestion.getOptions().get(3);

	AppServiceExtendImpl.getInstance().postQuestion(postParams, new OnPostQuestionResponseListener() {

	    @Override
	    public void onSuccess() {
		// TODO Auto-generated method stub
		showToast("问题已发送");
		questionDialog.dismiss();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub

	    }
	});
    }

    /**
     * 
     */

    private void showSearch() {
	//	Intent intent = new Intent(this.getActivity(), UsersSearchActivity.class);
	//	int requestCode = 2;
	//	startActivityForResult(intent, requestCode);
	if (mLayoutSearch.getVisibility() == View.VISIBLE) {
	    return;
	}
	AnimationSet as = new AnimationSet(true);
	TranslateAnimation translateAnmation = new TranslateAnimation(0, 0, -1000, 300);
	translateAnmation.setFillAfter(true);
	translateAnmation.setDuration(200L);
	as.addAnimation(translateAnmation);

	mLayoutSearch.startAnimation(as);

	mLayoutSearch.setVisibility(View.VISIBLE);
	//	if (null == searchDialog) {
	//	    searchDialog = new AlertDialog.Builder(this.getActivity()).create();
	//	    searchDialog.show();
	//
	//	    View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_dialog, null);
	//	    view.setBackgroundResource(R.drawable.rounder_corners);
	//	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	//	    searchDialog.addContentView(view, params);
	//	    searchDialog.getWindow().setGravity(Gravity.CENTER);
	//
	//	    // 默认全部性别
	//	    RadioButton rbAllsex = (RadioButton) view.findViewById(R.id.rbn_search_allsex);
	//	    rbAllsex.setChecked(true);
	//
	//	    view.findViewById(R.id.tv_search_confirm).setOnClickListener(new View.OnClickListener() {
	//
	//		@Override
	//		public void onClick(View v) {
	//		    searchDialog.cancel();
	//		    // 提交搜索
	//		}
	//	    });
	//	    view.findViewById(R.id.tv_search_cancle).setOnClickListener(new View.OnClickListener() {
	//
	//		@Override
	//		public void onClick(View v) {
	//		    searchDialog.cancel();
	//		}
	//	    });
	//	} else {
	//	    if (!searchDialog.isShowing()) {
	//		searchDialog.show();
	//	    }
	//	}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	switch (resultCode) {
	case 1:
	    String title = data.getStringExtra("question_title");
	    String option1 = data.getStringExtra("question_option1");
	    String option2 = data.getStringExtra("question_option2");
	    String option3 = data.getStringExtra("question_option3");
	    String option4 = data.getStringExtra("question_option4");
	    final EditText etQuestionTitle = (EditText) questionView.findViewById(R.id.et_question_title);
	    final EditText etQuestionOption1 = (EditText) questionView.findViewById(R.id.et_question_option1);
	    final EditText etQuestionOption2 = (EditText) questionView.findViewById(R.id.et_question_option2);
	    final EditText etQuestionOption3 = (EditText) questionView.findViewById(R.id.et_question_option3);
	    final EditText etQuestionOption4 = (EditText) questionView.findViewById(R.id.et_question_option4);

	    etQuestionTitle.setText(title);
	    etQuestionOption1.setText(option1);
	    etQuestionOption2.setText(option2);
	    etQuestionOption3.setText(option3);
	    etQuestionOption4.setText(option4);
	    break;
	}
	super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	position = position - 1;//mListView.getHeaderViewsCount();

	if (position <= -1 || position >= mAdapter.getList().size()) {
	    return;
	}
	NearbyUser user = mAdapter.getList().get(position);

	Intent intent = new Intent();
	intent.setClass(getActivity(), UserInfoTextActivity.class);
	intent.putExtra("user_id", user.getUserId());
	intent.putExtra("is_vip", user.getVip() == 1);
	intent.putExtra("user_nickname", user.getNickname());
	intent.putExtra("user_face_url", user.getFaceUrl().getSmallImageUrl());

	getActivity().startActivity(intent);

	// 开始对话
	//	RongIM.getInstance().startPrivateChat(getActivity(), user.getUserId() + "", user.getNickname());
    }

    @Override
    public void onClick(View arg0) {
	// TODO Auto-generated method stub
	switch (arg0.getId()) {
	case R.id.layout_search_homeplace:
	    selectHomeplace();
	    break;
	case R.id.layout_search_address:
	    selectAddress();
	    break;
	case R.id.layout_search_age:
	    selectWantedAge();
	    break;
	case R.id.layout_search_status:
	    selectWantedStauts();// 情感状态
	    break;
	case R.id.layout_search_salary:
	    selectWantedSalary();
	    break;
	case R.id.layout_search_house:
	    selectHouse();
	    break;
	case R.id.layout_search_height:
	    selectWantedHeight();
	    break;
	case R.id.layout_search_weight:
	    selectWantedWeight();
	    break;
	case R.id.layout_search_star:
	    selectWantedStar();
	    break;
	}
    }

    SelectProvinceCityPopupWindow cityPopupWindow;

    private void selectHomeplace() {

	//	
	if (null == cityPopupWindow) {
	    cityPopupWindow = new SelectProvinceCityPopupWindow(this.getActivity(), mTvSearchHomeplace.getText().toString());

	    cityPopupWindow.setOutsideTouchable(false);

	    cityPopupWindow.setFocusable(true);
	    cityPopupWindow.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    if (null == selectedCity) {
			mTvSearchHomeplace.setText("不限");
		    } else {
			mTvSearchHomeplace.setText(selectedCity);
		    }
		}
	    });
	}
	if (!cityPopupWindow.isShowing()) {
	    cityPopupWindow.showAtLocation(this.mContainerView, Gravity.CENTER, 0, 0);
	}
    }

    SelectProvinceCityPopupWindow cityPopupWindow2;

    private void selectAddress() {
	if (null == cityPopupWindow2) {
	    cityPopupWindow2 = new SelectProvinceCityPopupWindow(this.getActivity(), this.mTvSearchAddress.getText().toString());

	    cityPopupWindow2.setOutsideTouchable(false);

	    cityPopupWindow2.setFocusable(true);
	    cityPopupWindow2.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow2.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow2.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow2.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    if (null == selectedCity) {
			mTvSearchAddress.setText("不限");
		    } else {
			mTvSearchAddress.setText(selectedCity);
		    }
		}
	    });
	}
	if (!cityPopupWindow2.isShowing()) {
	    cityPopupWindow2.showAtLocation(this.mContainerView, Gravity.CENTER, 0, 0);

	}
    }

    /**
     * 年龄要求
     */
    private void selectWantedAge() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("年龄");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("20-23"));
	dialog.addMenuItem(dialog.new MenuItem("24-26"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("27-29"));
	dialog.addMenuItem(dialog.new MenuItem("30-32"));
	dialog.addMenuItem(dialog.new MenuItem("33-35"));
	dialog.addMenuItem(dialog.new MenuItem("36-40"));
	dialog.addMenuItem(dialog.new MenuItem("41-45"));
	dialog.addMenuItem(dialog.new MenuItem("46-50"));
	dialog.addMenuItem(dialog.new MenuItem("51-60"));

	dialog.setChecked(mTvSearchAge.getText().toString());
	dialog.setInputContent(mTvSearchAge.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchAge.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchAge.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 情感状态要求
     */
    private void selectWantedStauts() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("情感状态");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("单身"));
	dialog.addMenuItem(dialog.new MenuItem("恋爱中"));
	dialog.addMenuItem(dialog.new MenuItem("已婚"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("离异"));// 

	dialog.setChecked(mTvSearchSatus.getText().toString());
	dialog.setInputContent(mTvSearchSatus.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchSatus.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchSatus.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 最低收入
     */
    private void selectWantedSalary() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("月薪");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("2000以内"));
	dialog.addMenuItem(dialog.new MenuItem("2000-5000"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("5000-10000"));
	dialog.addMenuItem(dialog.new MenuItem("10000-20000"));
	dialog.addMenuItem(dialog.new MenuItem("20000-30000"));
	dialog.addMenuItem(dialog.new MenuItem("30000以上"));

	dialog.setChecked(mTvSearchSalary.getText().toString());
	dialog.setInputContent(mTvSearchSalary.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchSalary.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchSalary.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 
     */
    private void selectHouse() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("住房");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("已购房"));
	dialog.addMenuItem(dialog.new MenuItem("租房"));// 只有添加的第一个是选中状态

	dialog.setChecked(mTvSearchHouse.getText().toString());
	dialog.setInputContent(mTvSearchHouse.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchHouse.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchHouse.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    /**
     * 身高要求
     */
    private void selectWantedHeight() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("身高");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("150-165"));
	dialog.addMenuItem(dialog.new MenuItem("165-170"));
	dialog.addMenuItem(dialog.new MenuItem("170-175"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("175-180"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("180-190"));
	dialog.addMenuItem(dialog.new MenuItem("190以上"));

	dialog.setChecked(mTvSearchHeight.getText().toString());
	dialog.setInputContent(mTvSearchHeight.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchHeight.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchHeight.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 体重要求
     */
    private void selectWantedWeight() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("年龄要求");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("150-165"));
	dialog.addMenuItem(dialog.new MenuItem("165-170"));
	dialog.addMenuItem(dialog.new MenuItem("170-175"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("175-180"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("180-190"));
	dialog.addMenuItem(dialog.new MenuItem("190以上"));

	dialog.setChecked(mTvSearchWeight.getText().toString());
	dialog.setInputContent(mTvSearchWeight.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchWeight.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchWeight.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 星座要求
     */
    private void selectWantedStar() {
	ListMenuDialog dialog = new ListMenuDialog(this.getActivity(), false); // 单选模式
	dialog.setTitle("星座要求");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("150-165"));
	dialog.addMenuItem(dialog.new MenuItem("165-170"));
	dialog.addMenuItem(dialog.new MenuItem("170-175"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("175-180"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("180-190"));
	dialog.addMenuItem(dialog.new MenuItem("190以上"));

	dialog.setChecked(mTvSearchStar.getText().toString());
	dialog.setInputContent(mTvSearchStar.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    mTvSearchStar.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			mTvSearchStar.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }
}
