package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.MyQuestion;
import me.himi.love.ui.base.BaseActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 编辑创建问题
 * @ClassName:EditMyQuestionActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class EditMyQuestionActivity extends BaseActivity {

    private EditText etQuestionTitle;
    private EditText etQuestionOptions1;
    private EditText etQuestionOptions2;
    private EditText etQuestionOptions3;
    private EditText etQuestionOptions4;

    private TextView tvTopTitle, tvTopAction;

    private List<String> options = new ArrayList<String>();

    private boolean isUpdate;//是否为修改
    private int questionId;//修改的问题的Id

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_edit_my_question);

	init();

    }

    private void init() {
	questionId = getIntent().getIntExtra("question_id", 0);
	isUpdate = questionId != 0;

	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);

	tvTopTitle.setText("编辑问题");
	tvTopTitle.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("√");
	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		saveMyQuestion();
	    }
	});

	etQuestionTitle = (EditText) findViewById(R.id.et_question_title);
	etQuestionOptions1 = (EditText) findViewById(R.id.et_question_option1);
	etQuestionOptions2 = (EditText) findViewById(R.id.et_question_option2);
	etQuestionOptions3 = (EditText) findViewById(R.id.et_question_option3);
	etQuestionOptions4 = (EditText) findViewById(R.id.et_question_option4);

	if (isUpdate) {
	    String title = getIntent().getStringExtra("question_title");
	    String option1 = getIntent().getStringExtra("question_option1");
	    String option2 = getIntent().getStringExtra("question_option2");
	    String option3 = getIntent().getStringExtra("question_option3");
	    String option4 = getIntent().getStringExtra("question_option4");

	    etQuestionTitle.setText(title);
	    etQuestionOptions1.setText(option1);
	    etQuestionOptions2.setText(option2);
	    etQuestionOptions3.setText(option3);
	    etQuestionOptions4.setText(option4);
	}

    }

    private void saveMyQuestion() {
	if (TextUtils.isEmpty(etQuestionTitle.getText())) {
	    showToast("问题不能为空");
	    etQuestionTitle.requestFocus();
	    return;
	}

	if (TextUtils.isEmpty(etQuestionOptions1.getText())) {
	    showToast("问题选项1不能为空");
	    etQuestionOptions1.requestFocus();
	    return;
	}
	if (TextUtils.isEmpty(etQuestionOptions2.getText())) {
	    showToast("问题选项2不能为空");
	    etQuestionOptions2.requestFocus();
	    return;
	}

	options.clear();

	DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);
	MyQuestion myQuestion = new MyQuestion();

	myQuestion.setTitle(etQuestionTitle.getText().toString());
	myQuestion.setCreateTime((int) (System.currentTimeMillis() / 1000L));

	options.add(etQuestionOptions1.getText().toString());
	options.add(etQuestionOptions2.getText().toString());
	options.add(etQuestionOptions3.getText().toString());
	options.add(etQuestionOptions4.getText().toString());

	myQuestion.setOptions(options);
	if (isUpdate) {
	    myQuestion.setId(questionId);

	    dbHelper.updateMyQuestion(myQuestion);
	} else {
	    dbHelper.insertMyQuestion(myQuestion);
	}
	finish();
    }

}
