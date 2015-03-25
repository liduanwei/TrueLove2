package me.himi.love.entity.loader;

import java.util.List;

import me.himi.love.entity.ReceivedFans;

/**
 * @ClassName:IReceivedFansLoader
 * @author sparklee liduanwei_911@163.com
 * @date Nov 22, 2014 11:52:30 PM
 */
public interface IReceivedFansLoader {

    List<ReceivedFans> load(String resposne);
}
