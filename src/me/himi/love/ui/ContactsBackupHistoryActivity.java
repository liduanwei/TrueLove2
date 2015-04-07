package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.ContactsBackupAdapter;
import me.himi.love.entity.Article;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUtils;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.list.XListView.IXListViewListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:ContactsBackupHistoryActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ContactsBackupHistoryActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    me.himi.love.view.list.XListView mListView;

    ContactsBackupAdapter mAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_contacts_backup_history);

	init();
    }

    private float mLastTouchedX, mLastTouchedY;

    private void init() {
	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	TextView tvTopAction = getViewById(R.id.tv_top_action);

	tvTopTitle.setText("备份历史");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("");

	mListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview);
	mAdapter = new ContactsBackupAdapter(this, new ArrayList<ContactBackup>());

	mListView.setAdapter(mAdapter);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadBackupHistories();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadBackupHistories();
	    }
	});

	mListView.setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		mLastTouchedX = arg1.getRawX();
		mLastTouchedY = arg1.getRawY();
		return false;
	    }
	});

	mListView.setOnItemClickListener(this);
	loadFromCache();
    }

    // 使用本地缓存
    private final String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/contacts_backup_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    private void loadFromCache() {
	// TODO Auto-generated method stub
	List<ContactBackup> userNews = CacheUtils.loadFromCache(cachePath);
	if (userNews != null) {
	    mAdapter.setList(userNews);
	} else {
	    loadBackupHistories();
	}

    }

    int pageNumber = 1;

    private void loadBackupHistories() {
	// TODO Auto-generated method stub
	String url = Constants.URL_CONTACTS_BACKUP_HISTORIES;
	RequestParams params = new RequestParams();
	params.put("page", pageNumber + "");
	params.put("page_size", 20 + "");

	HttpUtil.post(url, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String data = new String(arg2);
		System.out.println("备份历史:" + data);
		List<ContactBackup> backups = new ArrayList<ContactBackup>();
		try {
		    JSONArray jsonArr = new JSONArray(data);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			int id = jsonObj.getInt("id");
			int size = jsonObj.getInt("size");
			int time = jsonObj.getInt("addtime");

			ContactBackup cb = new ContactBackup();
			cb.id = id;
			cb.contactsSize = size;
			cb.addtime = time;
			backups.add(cb);
		    }

		} catch (Throwable th) {
		    showToast("参数错误");
		    return;
		}

		if (backups.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(backups);
		    CacheUtils.cacheToLocal(mAdapter.getList(), cachePath);
		} else {
		    if (mAdapter.getList().size() == 0) {
			ActivityUtil.show(ContactsBackupHistoryActivity.this, "暂无备份");
		    } else {
			ToastFactory.getToast(ContactsBackupHistoryActivity.this, "暂无更多").show();
		    }
		}
		pageNumber++;
		//
		mListView.stopLoadMore();
		mListView.stopRefresh();

		//		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("连接超时");
		//
		mListView.stopLoadMore();
		mListView.stopRefresh();
	    }
	});
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_restoreFromHistories:
	    break;
	}
    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
    }

    public static class ContactBackup {
	public int id;
	public int contactsSize;
	public int addtime;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	// TODO Auto-generated method stub
	position = position - 1;//mListView.getHeaderViewsCount();

	if (position <= -1 || position >= mAdapter.getList().size()) {
	    return;
	}
	// 弹出操作菜单
	showMenu(mAdapter.getList().get(position), position, arg1);
    }

    private View menuView;
    private PopupWindow pwMenuWin;

    private void showMenu(final ContactBackup backup, int pos, View v) {
	// TODO Auto-generated method stub
	if (null == pwMenuWin) {
	    menuView = getLayoutInflater().inflate(R.layout.layout_contacts_backup_resotre_menu, null);
	    pwMenuWin = new PopupWindow(menuView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
	    pwMenuWin.setContentView(menuView);
	}
	if (pwMenuWin.isShowing()) {
	    pwMenuWin.dismiss();
	}

	int[] location = new int[2];
	v.getLocationOnScreen(location);
	pwMenuWin.showAtLocation(v, Gravity.NO_GRAVITY, (int) mLastTouchedX + 250/*v.getWidth() - 88*/, (int) mLastTouchedY);
	//	pwMenuWin.showAtLocation(v, Gravity.NO_GRAVITY, (int) touchedX, location[1] + v.getHeight() / 2);
	//	pwMenuWin.showAsDropDown(mListView.getChildAt(pos), 0, 0, Gravity.CENTER);
	//	pwMenuWin.showAsDropDown(v);

	final String url = me.himi.love.util.Constants.URL_CONTACTS_RESTORE;
	final RequestParams params = new RequestParams();

	final AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		showToast("操作成功");
		pwMenuWin.dismiss();
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("操作失败");
		pwMenuWin.dismiss();
	    }
	};

	// 恢复
	menuView.findViewById(R.id.btn_backup_restore).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		params.put("backup_id", backup.id);
		HttpUtil.post(url, params, responseHandler);
	    }
	});

	// 取消
	menuView.findViewById(R.id.btn_backup_cancel).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		pwMenuWin.dismiss();
	    }
	});

	final TextView tvContent = (TextView) menuView.findViewById(R.id.tv_title);
	tvContent.setText(backup.id + "");
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (pwMenuWin != null && pwMenuWin.isShowing()) {
	    pwMenuWin.dismiss();
	    return;
	}
	super.onBackPressed();
    }

}
