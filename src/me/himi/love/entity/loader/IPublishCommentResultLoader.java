package me.himi.love.entity.loader;

import java.util.List;

import me.himi.love.entity.UserNewsComment;

/**
 *  解析 发布评论后服务器的响应
 * @ClassName:IRecommendUserLoader
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:00:59 PM
 */
public interface IPublishCommentResultLoader {
    UserNewsComment load(String response);
}
