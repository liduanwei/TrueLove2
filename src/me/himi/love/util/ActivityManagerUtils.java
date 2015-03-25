package me.himi.love.util;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;

/**
 * @author kingofglory
 *         email: kingofglory@yeah.net
 *         blog:  http:www.google.com
 * @date 2014-2-21
 * TODO Activity收集以及释放
 */

public class ActivityManagerUtils {

    private ArrayList<Activity> activityList = new ArrayList<Activity>();

    private static ActivityManagerUtils activityManagerUtils;

    private ActivityManagerUtils() {

    }

    public static ActivityManagerUtils getInstance() {
	if (null == activityManagerUtils) {
	    activityManagerUtils = new ActivityManagerUtils();
	}
	return activityManagerUtils;
    }

    public Activity getTopActivity() {
	if (activityList.size() == 0) {
	    return null;
	}
	return activityList.get(activityList.size() - 1);
    }

    public void addActivity(Activity ac) {
	activityList.add(ac);
    }

    public void removeActivity(Activity ac) {
	activityList.remove(ac);
	if (ac.isFinishing()) {
	    ac.finish();
	}

    }

    public void removeAllActivity() {

	//	Iterator<Activity> its = activityList.iterator();
	//	while (its.hasNext()) {
	//	    Activity act = its.next();
	//	    if (!act.isFinishing()) {
	//		act.finish();
	//	    }
	//	    its.remove();
	//	    act = null;
	//	}

	for (Activity ac : activityList) {
	    if (null != ac) {
		if (!ac.isFinishing()) {
		    ac.finish();
		}
		ac = null;
	    }
	}

	activityList.clear();
    }
}
