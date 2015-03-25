package me.himi.love.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 好友/关注/附近 人的最新留言,最新帖子
 * @ClassName:NewsUpdateFragmentPagerAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 20, 2014 1:08:11 AM
 */
public class NewsUpdateFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public NewsUpdateFragmentPagerAdapter(FragmentManager fm) {
	super(fm);
	// TODO Auto-generated constructor stub
    }

    public NewsUpdateFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
	super(fm);
	this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
	// TODO Auto-generated method stub
	return fragments.get(arg0);
    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
	// TODO Auto-generated method stub
	return super.getItemPosition(object);
    }

}
