package me.himi.love.ui.fragment.base;

import me.himi.love.MyApplication;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName:BaseFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:53:04 PM
 */
public class BaseFragment extends Fragment {

    protected int mScreenWidth, mScreenHeight;

    public void showLog(String message) {
	Log.i("", message);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	DisplayMetrics metrics = new DisplayMetrics();
	getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

	this.mScreenWidth = metrics.widthPixels;
	this.mScreenHeight = metrics.heightPixels;
    }

    public void showToast(CharSequence text) {
	Toast.makeText(MyApplication.getInstance().getApplicationContext(), text, 0).show();
    }
    
    

}
