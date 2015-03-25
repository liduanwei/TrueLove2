package me.himi.love.util;

import java.util.HashMap;
import java.util.Map;

import me.himi.love.entity.DetailInfoUser;

/**
 * @ClassName:CacheUserManager
 * @author sparklee liduanwei_911@163.com
 * @date Nov 8, 2014 1:36:58 PM
 */
public class CacheUserManager {

    private static CacheUserManager instance;
    // 使用 weakhashmap 的字符串key不能 userId+""(创建新字符串对象), 否则无法获取
    private static Map<String, CacheDetailInfoUser> cachedDetailInfoUsers;//

    //    private static int MAX_COUNT = 50; // 最大缓存数

    public static CacheUserManager getInstance() {
	if (null == instance) {
	    instance = new CacheUserManager();
	}
	return instance;
    }

    private CacheUserManager() {
	cachedDetailInfoUsers = new /*Weak*/HashMap<String, CacheDetailInfoUser>();
    }

    public void addUser(String key, DetailInfoUser user, long expireTime) {
	if (cachedDetailInfoUsers.containsKey(key)) {
	    cachedDetailInfoUsers.get(key).destroy();
	    cachedDetailInfoUsers.remove(key);
	}

	System.out.println("adduser" + key);

	CacheDetailInfoUser detailUser = new CacheDetailInfoUser(user, expireTime);
	cachedDetailInfoUsers.put(key, detailUser);
    }

    /**
     * 使用方法 getUser 不为 null 则存在缓存(需要判断是否过期,为 null 则未缓存
     * @param key
     * @return
     */
    public CacheDetailInfoUser getUser(String key) {
	if (!cachedDetailInfoUsers.containsKey(key)) {
	    System.out.println("未缓存:" + key);
	    return null;
	}

	CacheDetailInfoUser cachedUser = cachedDetailInfoUsers.get(key);
	//	if (cachedUser.isExpired()) {
	//	    cachedUser.destroy();
	//	    return null;
	//	}
	return cachedUser;

    }

    /**
     *  清除指定key的缓存对象
     * @param key
     */
    public void clearUser(String key) {
	if (getUser(key) != null) {
	    cachedDetailInfoUsers.remove(key);
	}
    }

    /**
     * 清空所有缓存对象
     */
    public void clearAllCache() {
	if (cachedDetailInfoUsers == null) {
	    return;
	}
	cachedDetailInfoUsers.clear();
	cachedDetailInfoUsers = null;
	instance = null; // 自身实例同时置null
    }

    public static class CacheDetailInfoUser {
	private long expireTime;
	private DetailInfoUser user;

	public CacheDetailInfoUser(DetailInfoUser user, long expireTime) {
	    this.user = user;
	    this.expireTime = System.currentTimeMillis() + expireTime;

	}

	public boolean isExpired() {
	    return System.currentTimeMillis() - this.expireTime >= 0;
	}

	public void setExpireTime(long expireTime) {
	    this.expireTime = System.currentTimeMillis() + expireTime;
	}

	public DetailInfoUser getUser() {
	    return user;
	}

	public void destroy() {
	    this.user = null;
	}
    }

}
