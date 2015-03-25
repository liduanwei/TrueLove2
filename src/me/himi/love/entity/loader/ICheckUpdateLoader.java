package me.himi.love.entity.loader;

import me.himi.love.entity.CheckUpdateVersion;

/**
 * @ClassName:IRecommendUserLoader
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:00:59 PM
 */
public interface ICheckUpdateLoader {
    CheckUpdateVersion load(String response);
}
