package me.himi.love.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * @ClassName:ContactsHelper
 * @author sparklee liduanwei_911@163.com
 * @date Apr 6, 2015 1:37:46 PM
 */

public class ContactsHelper {

    private ContentResolver contentResolver;

    private static ContactsHelper instance;

    private Context context;

    private ContactsHelper(Context context) {
	this.context = context;
	contentResolver = context.getContentResolver();
    }

    public static ContactsHelper getInstance(Context context) {
	if (null == instance) {
	    instance = new ContactsHelper(context);
	}
	return instance;
    }

    private static final String[] CONTACT_POJECTION = { Phone.DISPLAY_NAME, Phone.NUMBER };

    private List<Contact> contacts = new ArrayList<Contact>();

    public List<Contact> getContacts() {
	contacts.clear();

	Cursor cursor = this.contentResolver.query(Phone.CONTENT_URI, CONTACT_POJECTION, null, null, null);

	if (cursor == null) {
	    return contacts;
	}

	while (cursor.moveToNext()) {
	    String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));

	    if (number.length() == 0) {
		continue;
	    }

	    String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));

	    Contact contact = new Contact();
	    contacts.add(contact);
	    contact.setNumber(number);
	    contact.setDisplayName(name);

	}
	return contacts;
    }

    /**
     * 插入联系人到手机联系人数据库中
     * @param contacts
     */
    public void insertToPhone(final List<Contact> contacts, final OpCallback callback) {
	final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
		    if (callback != null) {
			callback.onSuccess(msg.arg1);
		    }
		    break;
		}
	    }
	};

	new Thread() {
	    public void run() {
		int successCount = 0;
		for (Contact c : contacts) {
		    insertContact(context, c.getDisplayName(), c.getNumber());
		    successCount++;
		}
		handler.obtainMessage(0, successCount, 0).sendToTarget();
	    };
	}.start();

    }

    public static interface OpCallback {
	void onSuccess(int count);

	void onFailure(String errormsg);
    }

    private Uri insertContact(Context context, String name, String phone) {
	ContentValues values = new ContentValues();

	values.put(People.NAME, name);

	Uri uri = context.getContentResolver().insert(People.CONTENT_URI, values);

	Uri numberUri = Uri.withAppendedPath(uri, People.Phones.CONTENT_DIRECTORY);

	values.clear();

	values.put(People.Phones.TYPE, People.Phones.TYPE_MOBILE);

	values.put(People.NUMBER, phone);

	context.getContentResolver().insert(numberUri, values);
	return uri;

    }

    public static class Contact {
	private String number;
	private String displayName;

	public Contact() {

	}

	public String getDisplayName() {
	    return displayName;
	}

	public void setDisplayName(String displayName) {
	    this.displayName = displayName;
	}

	public String getNumber() {
	    return number;
	}

	public void setNumber(String number) {
	    this.number = number;
	}

	@Override
	public String toString() {
	    return this.displayName + "," + this.number;
	}
    }

}
