package me.himi.love.ui.sound;

import me.himi.love.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * @ClassName:SoundPlayer
 * @author sparklee liduanwei_911@163.com
 * @date Feb 4, 2015 10:58:25 PM
 */
public class SoundPlayer {
    private static SoundPlayer instance;

    private Context mContext;

    private SoundPlayer(Context context) {
	mContext = context;
	init();
    }

    public static SoundPlayer getInstance(Context context) {
	if (null == instance) {
	    instance = new SoundPlayer(context);

	}
	return instance;
    }

    private SoundPool soundPool;
    private int okId;

    private int notify;

    private void init() {
	// TODO Auto-generated method stub
	this.soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
	this.okId = soundPool.load(mContext, R.raw.ok, 1);
	this.notify = soundPool.load(mContext, R.raw.notify, 1);
    }

    public void playOk() {
	this.soundPool.play(okId, 1.0f, 1.0f, 0, 0, 2.0f);
    }

    public void playNotify() {
	this.soundPool.play(notify, 1.0f, 1.0f, 0, 0, 2.0f);
    }

}
