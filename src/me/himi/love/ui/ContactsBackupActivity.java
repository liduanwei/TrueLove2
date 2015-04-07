package me.himi.love.ui;

import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.BackupContactsPostParams;
import me.himi.love.IAppServiceExtend.OnBackupContactsResponseListener;
import me.himi.love.IAppServiceExtend.OnRestoreContactsResponseListener;
import me.himi.love.IAppServiceExtend.RestoreContactsPostParams;
import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.ContactsHelper;
import me.himi.love.util.ContactsHelper.Contact;
import me.himi.love.util.ContactsHelper.OpCallback;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:ContactsBackupActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ContactsBackupActivity extends BaseActivity implements OnClickListener {

    TextView mTvContactsCount, mTvLastTime, mTvLastServerSize;

    TextView mTvRestoreFromHistories;

    ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_contacts_backup);

	init();
    }

    private void init() {
	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	TextView tvTopAction = getViewById(R.id.tv_top_action);

	tvTopTitle.setText("联系人备份");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("");

	findViewById(R.id.layout_backup).setOnClickListener(this);
	findViewById(R.id.layout_restore).setOnClickListener(this);

	mTvContactsCount = getViewById(R.id.tv_contacts_backup_no);
	mTvLastTime = getViewById(R.id.tv_backup_last_time); // 上次备份时间
	mTvLastServerSize = getViewById(R.id.tv_contacts_backup_last_server_no); // 上次备份 服务器上的联系人数量
	mTvRestoreFromHistories = getViewById(R.id.tv_restoreFromHistories); // 查看备份历史
	mPbLoading = getViewById(R.id.pb_loading);

	mTvLastTime.setText("");

	mTvRestoreFromHistories.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.layout_backup:
	    backup();
	    break;
	case R.id.layout_restore:
	    if (mPbLoading.getVisibility() == View.VISIBLE) {
		showToast("读取中,请稍候");
		return;
	    }
	    if (!isBackupExists) {
		showToast("您还没有备份,建议立即备份");
		return;
	    }
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    final AlertDialog dialog = builder.create();

	    builder.setMessage("此操作可能会造成已有联系人资料重复,确认从服务器恢复联系人到您的手机上");
	    builder.setNegativeButton("恢复", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
		    // TODO Auto-generated method stub
		    restore();
		}
	    });

	    builder.setPositiveButton("取消", null);
	    builder.show();

	    break;
	case R.id.tv_restoreFromHistories:
	    startActivity(new Intent(ContactsBackupActivity.this, ContactsBackupHistoryActivity.class));
	    break;
	}
    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	List<Contact> contacts = ContactsHelper.getInstance(this).getContacts();
	mTvContactsCount.setText(contacts.size() + " 联系人");
	loadLast();
    }

    private boolean isBackupExists; // 服务器上是否存在备份

    private void loadLast() {
	// TODO Auto-generated method stub
	String url = Constants.URL_CONTACTS_BACKUP_LAST;
	RequestParams params = new RequestParams();
	HttpUtil.post(url, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String data = new String(arg2);
		System.out.println("上次备份" + data);
		try {
		    JSONObject jsonObj = new JSONObject(data);
		    int time = jsonObj.getInt("time");
		    int size = jsonObj.getInt("size");
		    if (time == 0 || size == 0) {
			mTvLastTime.setTextColor(getResources().getColor(R.color.c_f98800));
			mTvLastTime.setText("您还没有进行过备份,建议立即备份以防止数据意外丢失");
			mPbLoading.setVisibility(View.GONE);

			isBackupExists = false;
			return;
		    }
		    mTvLastServerSize.setText(size + " 联系人");
		    String timeStr = ActivityUtil.getTimeStr("yyyy年MM月dd日 HH:mm:ss", time);
		    mTvLastTime.setTextColor(getResources().getColor(R.color.text_gray));
		    mTvLastTime.setText("上次备份时间:" + timeStr);

		    isBackupExists = true;
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		mPbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("连接出错");
		mPbLoading.setVisibility(View.GONE);
	    }
	});
    }

    ProgressDialog progressDialog;

    private void backup() {
	// TODO Auto-generated method stub
	if (null == progressDialog) {
	    progressDialog = new ProgressDialog(this);
	}

	progressDialog.setMessage("上传数据中...");
	progressDialog.show();

	List<Contact> contacts = ContactsHelper.getInstance(this).getContacts();

	// 测试大数据量
	//	for (int i = 0; i < 10000; ++i) {
	//	    Contact c = new Contact();
	//	    c.setDisplayName("TESTER");
	//	    c.setNumber("+8613418672673");
	//	    contacts.add(c);
	//	}

	// 测试恢复功能
	//	contacts.clear();
	//	Contact c = new Contact();
	//	c.setDisplayName("TESTER");
	//	c.setNumber("+8613418672673");
	//	contacts.add(c);

	String jsonStr = "";
	try {
	    jsonStr = contactsToJson(contacts);//.replace("\"", "");
	} catch (Throwable th) {
	    showToast("备份失败, 无法读取联系人数据");
	    return;
	}

	BackupContactsPostParams postParams = new BackupContactsPostParams();
	postParams.contacts = jsonStr;
	postParams.size = contacts.size();

	AppServiceExtendImpl.getInstance().backupContacts(postParams, new OnBackupContactsResponseListener() {

	    @Override
	    public void onSuccess(int time, int size) {

		progressDialog.dismiss();

		// TODO Auto-generated method stub
		if (time == 0 || size == 0) {
		    mTvLastTime.setTextColor(getResources().getColor(R.color.c_f98800));
		    mTvLastTime.setText("您还没有进行过备份,建议立即备份以防止数据意外丢失");

		    isBackupExists = false;
		    return;
		}
		String timeStr = ActivityUtil.getTimeStr("yyyy年MM月dd日 HH:mm:ss", time);
		mTvLastTime.setTextColor(getResources().getColor(R.color.text_gray));
		mTvLastTime.setText("上次备份时间:" + timeStr);
		mTvLastServerSize.setText(size + " 联系人");
		isBackupExists = true;
		showToast("备份成功!");
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		showToast(errorMsg);
	    }
	});
    }

    private String contactsToJson(List<Contact> contacts) throws JSONException {
	// TODO Auto-generated method stub

	JSONArray jsonArr = new JSONArray();

	for (Contact contact : contacts) {
	    JSONObject jsonObj = new JSONObject();
	    jsonObj.put("name", contact.getDisplayName());
	    jsonObj.put("number", contact.getNumber());

	    jsonArr.put(jsonObj);
	}

	return jsonArr.toString();
    }

    private void restore() {
	if (null == progressDialog) {
	    progressDialog = new ProgressDialog(this);
	}
	progressDialog.setMessage("下载数据中...");
	progressDialog.show();

	RestoreContactsPostParams postParams = new RestoreContactsPostParams();

	AppServiceExtendImpl.getInstance().restoreContacts(postParams, new OnRestoreContactsResponseListener() {

	    @Override
	    public void onSuccess(List<Contact> contacts) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();

		if (contacts != null) {
		    if (contacts.size() == 0) {
			showToast("未备份过");
			return;
		    }
		    ContactsHelper.getInstance(ContactsBackupActivity.this).insertToPhone(contacts, new OpCallback() {

			@Override
			public void onSuccess(int count) {
			    // TODO Auto-generated method stub
			    progressDialog.dismiss();
			    showToast("联系人已恢复成功!");
			}

			@Override
			public void onFailure(String errormsg) {
			    // TODO Auto-generated method stub
			    progressDialog.dismiss();
			}
		    });
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		showToast(errorMsg);
	    }
	});

	// 下载联系人数据,插入到本地手机联系人数据库中

    }
}
