package me.himi.love.boost.androidservice;

import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.util.Share;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

/**
 * @ClassName:FloatShareService
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 3:03:07 PM
 */
public class FloatShareService extends Service {

    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x, startX;
    private float y, startY;

    private OnFloatClickListener onFloatClickListener;

    @Override
    public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return new FloatShareBinder();
    }

    public class FloatShareBinder extends Binder {
	FloatShareService getService() {
	    return FloatShareService.this;
	}
    }

    @Override
    public void onCreate() {
	super.onCreate();

	view = LayoutInflater.from(this).inflate(R.layout.float_share, null);

	createView();
    }

    private boolean isClicked; // 判断是否为点击

    private void createView() {
	// 获取WindowManager
	wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	// 设置LayoutParams(全局变量）相关参数
	wmParams = new WindowManager.LayoutParams();
	wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
	wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
	wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
	// 以屏幕左上角为原点，设置x、y初始值
	SharedPreferences pref = getSharedPreferences("float_location", Context.MODE_PRIVATE);

	wmParams.x = pref.getInt("last_x", 200);
	wmParams.y = pref.getInt("last_y", 200);
	// 设置悬浮窗口长宽数据
	wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
	wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
	wmParams.format = PixelFormat.TRANSLUCENT;

	wm.addView(view, wmParams);

	view.setOnTouchListener(new OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		// 25是系统状态栏的高度,也可以通过方法得到准确的值，自己微调就是了
		y = event.getRawY() - 25/*ActivityUtil.getStatusBarHeight(MyApplication.getInstance().getTopActivity())*/;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    // 获取相对View的坐标，即以此View左上角为原点
		    mTouchStartX = event.getX();
		    mTouchStartY = event.getY() + view.getHeight() / 2;

		    startX = event.getRawX();
		    startY = event.getRawY() - 25;
		    isClicked = true;
		    break;
		case MotionEvent.ACTION_MOVE:
		    updateViewPosition();

		    isClicked = Math.abs(x - startX) < 20 && Math.abs(y - startY) < 20; // 移动过为非点击
		    break;
		case MotionEvent.ACTION_UP:
		    updateViewPosition();
		    mTouchStartX = mTouchStartY = 0;
		    // 保存最后位置
		    SharedPreferences pref = getSharedPreferences("float_location", Context.MODE_PRIVATE);
		    pref.edit().putInt("last_x", wmParams.x).putInt("last_y", wmParams.y).commit();

		    startX = startY = 0;
		    if (isClicked) {
			//			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//			startActivity(intent);
			//

			if (onFloatClickListener != null) {
			    onFloatClickListener.onClick();
			}
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			//获得当前运行的task  
			List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(100);
			for (ActivityManager.RunningTaskInfo rti : taskList) {
			    //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台  
			    if (rti.topActivity.getPackageName().contains(getPackageName())) {
				// 移动任务到前台
				try {
				    Intent intent = new Intent(getBaseContext(), Class.forName(rti.topActivity.getClassName()));
				    // FLAG_ACTIVITY_SINGLE_TOP 不创建新activity
				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				    startActivity(intent);
				} catch (Throwable th) {

				}
				//								am.moveTaskToFront(rti.id, 0);
				break;
			    }
			}

			//			try {
			//			    Intent intent = new Intent(getBaseContext(), Class.forName(MainActivity.class.getName()));
			//			    // FLAG_ACTIVITY_SINGLE_TOP 不创建新activity
			//			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//			    startActivity(intent);
			//			} catch (Throwable th) {
			//
			//			}
			//

			if (MyApplication.getInstance().getTopActivity() != null) {
			    // 查看会话
			    //RongIM.getInstance().startConversationList(getApplicationContext());

			    Share.share(MyApplication.getInstance().getTopActivity(), "我正在使用" + getResources().getString(R.string.app_name) + "征婚交友APP,帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");
			}

		    }
		    break;
		}

		return false;
	    }
	});

	//	view.findViewById(R.id.tv_float_share).setOnClickListener(new View.OnClickListener() {
	//
	//	    @Override
	//	    public void onClick(View v) {
	//		// TODO Auto-generated method stub
	//		Share.share(MyApplication.getInstance().getTopActivity(), "我正在使用" + getResources().getString(R.string.app_name) + "征婚交友APP,助依然守候的你在茫茫人海中找到相守一生对的那个Ta");
	//	    }
	//	});
    }

    private void updateViewPosition() {
	// 更新浮动窗口位置参数
	wmParams.x = (int) (x - mTouchStartX);
	wmParams.y = (int) (y - mTouchStartY);

	wm.updateViewLayout(view, wmParams);
    }

    @Override
    public void onDestroy() {
	// TODO Auto-generated method stub
	wm.removeView(view);
	super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

	return super.onStartCommand(intent, flags, startId);
    }

    public OnFloatClickListener getOnFloatClickListener() {
	return onFloatClickListener;
    }

    public void setOnFloatClickListener(OnFloatClickListener onFloatClickListener) {
	this.onFloatClickListener = onFloatClickListener;
    }

    public static interface OnFloatClickListener {

	void onClick();
    }
}
