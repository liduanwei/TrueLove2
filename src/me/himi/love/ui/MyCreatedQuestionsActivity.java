package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.MyCreatedQuestionsAdapter;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.MyQuestion;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 用户已经创建的问题
 * @ClassName:EditMyQuestionActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyCreatedQuestionsActivity extends BaseActivity {

    TextView tvTopTitle, tvTopAction;

    XListView mListView;
    MyCreatedQuestionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_my_created_questions);

	init();

    }

    private void init() {
	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopTitle.setText("选取问题");
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("+");// 新建问题

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(MyCreatedQuestionsActivity.this, EditMyQuestionActivity.class);
		startActivity(intent);
	    }
	});

	mListView = (XListView) findViewById(R.id.listview_my_questions);

	// 
	mAdapter = new MyCreatedQuestionsAdapter(this, new ArrayList<MyQuestion>());

	mListView.setPullLoadEnable(true);
	mListView.setPullRefreshEnable(true);

	mListView.setAdapter(mAdapter); // 已经设置adapter ,也有数据,就是不展示 why?

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		pageNumber = 1;
		loadMyQuestions();
	    }

	    @Override
	    public void onLoadMore() {
		loadMyQuestions();
	    }
	});

	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position -= 1;
		mCurrentSelectedQuestion = mAdapter.getList().get(position);

		Intent intent = new Intent();
		intent.putExtra("question_id", mCurrentSelectedQuestion.getId());
		intent.putExtra("question_title", mCurrentSelectedQuestion.getTitle());
		intent.putExtra("question_option1", mCurrentSelectedQuestion.getOptions().get(0));
		intent.putExtra("question_option2", mCurrentSelectedQuestion.getOptions().get(1));
		intent.putExtra("question_option3", mCurrentSelectedQuestion.getOptions().get(2));
		intent.putExtra("question_option4", mCurrentSelectedQuestion.getOptions().get(3));
		setResult(1, intent);
		finish();
	    }
	});

	// 设定初始状态为刷新中
	mListView.pullRefreshing();

	mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo m = (AdapterContextMenuInfo) menuInfo;
		mCurrentSelectedQuestion = mAdapter.getList().get(m.position - 1);
		menu.add(0, 0, 0, "编辑");
		menu.add(0, 1, 1, "删除");
	    }
	});

	//已改为 onResume 时调用
	//	loadMyQuestions();
    }

    int pageNumber = 1;
    DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);

    /**
     * 从本地数据库中加载我的问题
     */
    private void loadMyQuestions() {
	// TODO Auto-generated method stub

	int limitSize = 20;
	int limitStart = (pageNumber - 1);

	List<MyQuestion> myQuestions = dbHelper.queryMyQuestions(limitStart, limitSize);
	dbHelper.close(); // 及时关闭数据库释放连接资源

	if (myQuestions == null) {
	    showToast("无数据");
	    return;
	}

	if (myQuestions.size() != 0) {
	    if (pageNumber == 1) {
		mAdapter.getList().clear();
	    }
	    mAdapter.getList().addAll(myQuestions);
	    mAdapter.notifyDataSetChanged();
	    pageNumber++;
	} else {
	    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "你还没有创建问题");
	}

	mListView.stopRefresh();
	mListView.stopLoadMore();

    }

    MyQuestion mCurrentSelectedQuestion;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case 0: // 编辑
	    Intent intent = new Intent(MyCreatedQuestionsActivity.this, EditMyQuestionActivity.class);
	    intent.putExtra("question_id", mCurrentSelectedQuestion.getId());
	    intent.putExtra("question_title", mCurrentSelectedQuestion.getTitle());
	    intent.putExtra("question_option1", mCurrentSelectedQuestion.getOptions().get(0));
	    intent.putExtra("question_option2", mCurrentSelectedQuestion.getOptions().get(1));
	    intent.putExtra("question_option3", mCurrentSelectedQuestion.getOptions().get(2));
	    intent.putExtra("question_option4", mCurrentSelectedQuestion.getOptions().get(3));
	    startActivity(intent);
	    break;
	case 1:
	    dbHelper.deleteMyQuestion(mCurrentSelectedQuestion.getId());
	    pageNumber = 1;
	    loadMyQuestions();
	    break;
	}
	return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
	//
	pageNumber = 1;
	loadMyQuestions();

	super.onResume();
    }
}
