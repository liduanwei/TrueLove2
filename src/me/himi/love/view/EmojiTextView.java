package me.himi.love.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.widget.TextView;

public class EmojiTextView extends TextView {
    private Context context;

    public EmojiTextView(Context context) {
	super(context);
	this.context = context;
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.context = context;
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	this.context = context;
    }

    public void setEmojiText(String text) {
	setText(text);
	
	
//	if (!text.contains("%%")) {
//	    setText(text);
//	    return;
//	}
	// 以下代码存在性能问题
//	text = EmojiUtils.convertTag(text);
//	CharSequence spanned = Html.fromHtml(text, emojiGetter, null);
//	setText(spanned);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
	// TODO Auto-generated method stub
	super.setText(text, type);
    }

    private ImageGetter emojiGetter = new ImageGetter() {
	public Drawable getDrawable(String source) {
	    // 读取资源文件ID
	    int id = getResources().getIdentifier(source, "drawable", context.getPackageName());
	    Drawable emoji = getResources().getDrawable(id);
	    int w = (int) (emoji.getIntrinsicWidth() * 1.01);
	    int h = (int) (emoji.getIntrinsicHeight() * 1.01);
	    emoji.setBounds(0, 0, w, h);
	    return emoji;
	}
    };
}