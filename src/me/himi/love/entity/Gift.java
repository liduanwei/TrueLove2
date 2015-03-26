package me.himi.love.entity;

/**
 * 
 * @ClassName:Gift
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class Gift implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int giftId;

    private String name;

    private String imageUrl;

    private String withWord; // 自身附带的赠言

    public int getGiftId() {
	return giftId;
    }

    public void setGiftId(int giftId) {
	this.giftId = giftId;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getWithWord() {
	return withWord;
    }

    public void setWithWord(String withWord) {
	this.withWord = withWord;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
