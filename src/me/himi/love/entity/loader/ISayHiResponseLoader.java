package me.himi.love.entity.loader;

import java.util.List;

import me.himi.love.entity.ReceivedSayHi;

/**
 * @ClassName:IRecommendUserLoader
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:00:59 PM
 */
public interface ISayHiResponseLoader {
    List<ReceivedSayHi> load(String response);
}
