package com.rushfusion.ad;


import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

//自定义文本标签，自动换行
public class MyView extends TextView {
	private Paint mPaint = new Paint();

	private String content;
	
	public MyView(Context context,String content) {
		super(context);
		this.content = content;
	}

	
	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	private void init() {
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED); 
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15));
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		
//		setMeasuredDimension(300, 200);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//view.draw()绘制了控件的背景
		

		//控件的绘制操作及顺序：
		/*
         * Draw traversal performs several drawing steps which must be executed
         * in the appropriate order:
         *
         *      1. Draw the background  （绘制控件设置的背景）
         *      2. If necessary, save the canvas' layers to prepare for fading  
         *      3. Draw view's content  (可以重写， onDraw(canvas);)
         *      4. Draw children      (可重写，用来分发画布到子控件，具体看ViewGroup。对应方法dispatchDraw(canvas);此方法依次调用了子控件的draw()方法)
         *      5. If necessary, draw the fading edges and restore layers （绘制控件四周的阴影渐变效果）
         *      6. Draw decorations (scrollbars for instance) （用来绘制滚动条，对应方法onDrawScrollBars(canvas);。
         *      可以重写onDrawHorizontalScrollBar()和onDrawVerticalScrollBar()来自定义滚动条）
         */
		
		//可以绘制内容和滚动条。
		
		//draw backgroud
		canvas.drawColor(Color.WHITE);
		
		//draw text
		FontMetrics fm = mPaint.getFontMetrics();
		
		float baseline = fm.descent - fm.ascent; 
		float x = 0;
		float y =  baseline;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
		
		String txt = content;//getResources().getString(com.rushfusion.ad.R.string.hello);
		
		//文本自动换行
		String[] texts = autoSplit(txt, mPaint, getWidth() - 5);
		
		System.out.printf("line indexs: %s\n", Arrays.toString(texts));
		
		for(String text : texts) { 
			canvas.drawText(text, x, y, mPaint);  //坐标以控件左上角为原点
			y += baseline + fm.leading; //添加字体行间距
		}
	}
	
	
	/**
	 * 自动分割文本
	 * @param content 需要分割的文本
	 * @param p  画笔，用来根据字体测量文本的宽度
	 * @param width 指定的宽度
	 * @return 一个字符串数组，保存每行的文本
	 */
	private String[] autoSplit(String content, Paint p, float width) {
		int length = content.length();
		float textWidth = p.measureText(content);
		if(textWidth <= width) {
			return new String[]{content};
		}
		
		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width); //计算行数
		String[] lineTexts = new String[lines];
		while(start < length) {
			if(p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
				lineTexts[i++] = (String) content.subSequence(start, end);
				start = end;
			}
			if(end == length) { //不足一行的文本
				lineTexts[i] = (String) content.subSequence(start, end);
				break;
			}
			end += 1;
		}
		return lineTexts;
	}
	
	/**
	 * 获取指定单位对应的原始大小（根据设备信息）
	 * px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit  TypedValue.COMPLEX_UNIT_*
	 * @param size
	 * @return
	 */
	public float getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
}
