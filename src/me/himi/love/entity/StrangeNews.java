package me.himi.love.entity;

/**
 * 奇闻
 * @ClassName:StrangeNews
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class StrangeNews implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2198663806892016620L;

    private int id;

    private String title, summary;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    private BSImageUrl contentImageUrl;
    private String tag;
    private int createTime;

    public String getTag() {
	return tag;
    }

    public void setTag(String tag) {
	this.tag = tag;
    }

    public int getCreateTime() {
	return createTime;
    }

    public void setCreateTime(int createTime) {
	this.createTime = createTime;
    }

    @Override
    public String toString() {
	return title;
    }

    public BSImageUrl getContentImageUrl() {
	return contentImageUrl;
    }

    public void setContentImageUrl(BSImageUrl contentImageUrl) {
	this.contentImageUrl = contentImageUrl;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

}
