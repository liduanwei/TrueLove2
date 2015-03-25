package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadQuestionsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadQuestionsResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.MyReceivedQuestionsAdapter;
import me.himi.love.entity.ReceivedQuestion;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 用户收到的提问
 * @ClassName:MySayhiMessagesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyReceivedQuestionsActivity extends BaseActivity {

    TextView tvTopTitle, tvTopAction;
    me.himi.love.view.list.XListView mListView;

    MyReceivedQuestionsAdapter mAdapter;

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_my_questions_messages);

	init();

    }

    private View popupView;

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	// 弹窗 view
	popupView = findViewById(R.id.popup_questions);

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

	    }
	});

	mListView = (XListView) findViewById(R.id.listview_my_questions_msges);
	mListView.setPullRefreshEnable(true);
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		pageNumber = 1;
		loadQuestions();
	    }

	    @Override
	    public void onLoadMore() {
		loadQuestions();
	    }
	});

	mAdapter = new MyReceivedQuestionsAdapter(this, new ArrayList<ReceivedQuestion>());

	mListView.setAdapter(mAdapter);
	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position -= 1;
		ReceivedQuestion question = mAdapter.getList().get(position);
		// 将这条提问更新为已读
		showPopupQuestion(question);
	    }
	});

	loadQuestions();

    }

    /**
     * 加载我收到的问题
     */
    private void loadQuestions() {
	LoadQuestionsPostParams postParams = new LoadQuestionsPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 10;
	AppServiceExtendImpl.getInstance().loadQuestions(postParams, new OnLoadQuestionsResponseListener() {

	    @Override
	    public void onSuccess(List<ReceivedQuestion> questions) {
		if (questions == null) {
		    return;
		}

		if (questions.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(questions);
		} else {
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无问题");
		}

		mListView.stopLoadMore();
		mListView.stopRefresh();

		pageNumber++;
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		mListView.stopLoadMore();
		mListView.stopRefresh();
		showToast(errorMsg);
	    }
	});
    }

    /**
     * 展示弹窗
     */
    private void showPopupQuestion(ReceivedQuestion question) {
	// 显示弹窗
	this.popupView.setVisibility(View.VISIBLE);
	TextView tvTitle = (TextView) this.popupView.findViewById(R.id.tv_question_title);
	TextView tvAnswerOption1 = (TextView) this.popupView.findViewById(R.id.tv_answer_option1);
	TextView tvAnswerOption2 = (TextView) this.popupView.findViewById(R.id.tv_answer_option2);
	TextView tvAnswerOption3 = (TextView) this.popupView.findViewById(R.id.tv_answer_option3);
	TextView tvAnswerOption4 = (TextView) this.popupView.findViewById(R.id.tv_answer_option4);

	tvTitle.setText(question.getTitle());
	tvAnswerOption1.setText(question.getOptions().get(0));
	tvAnswerOption2.setText(question.getOptions().get(1));

	if (!TextUtils.isEmpty(question.getOptions().get(2))) {
	    tvAnswerOption3.setText(question.getOptions().get(2));
	} else {
	    tvAnswerOption3.setVisibility(View.GONE);
	}
	if (!TextUtils.isEmpty(question.getOptions().get(3))) {
	    tvAnswerOption4.setText(question.getOptions().get(3));
	} else {
	    tvAnswerOption4.setVisibility(View.GONE);
	}

	// 回答1
	View answer01 = popupView.findViewById(R.id.tv_answer_option1);
	answer01.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hidePopupQuestion();
	    }
	});

	View answer02 = popupView.findViewById(R.id.tv_answer_option2);
	answer02.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hidePopupQuestion();

	    }
	});
	View answer03 = popupView.findViewById(R.id.tv_answer_option3);
	answer03.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hidePopupQuestion();
	    }
	});
	View answer04 = popupView.findViewById(R.id.tv_answer_option4);
	answer04.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hidePopupQuestion();
	    }
	});

	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 200, 0);
	transAnimation.setDuration(300);
	animationSet.addAnimation(transAnimation);
	popupView.startAnimation(animationSet);

    }

    /**
     * 隐藏弹窗
     */
    private void hidePopupQuestion() {
	this.popupView.setVisibility(View.GONE);
	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 0, 200);
	transAnimation.setDuration(500);
	AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.001f);
	alphaAnimation.setDuration(500);

	animationSet.addAnimation(transAnimation);
	animationSet.addAnimation(alphaAnimation);

	popupView.startAnimation(animationSet);
    }
}
