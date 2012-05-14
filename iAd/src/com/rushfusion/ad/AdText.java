package com.rushfusion.ad;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class AdText extends TextView {

	public AdText(Context context) {
		super(context);
	}

	public AdText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AdText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	} 

	public void setText(int w,int h,HashMap<String,String> text){
		String value = text.get("value").replace('\n', ' ').trim();
		FontMetrics fm = getPaint().getFontMetrics();
		float baseline = fm.descent - fm.ascent + fm.leading;
		System.out.println("getHeight-->"+h+" baseLine-->"+baseline);
		if(h<baseline){
			setText(value);
			return;
		}
		int maxLines = (int) Math.ceil((h/baseline)-1);
		String [] strs = getValuesByLines(w,value,maxLines);
		for(String s:strs){
			System.out.println("s-->"+s);
		}
		String anim = text.get("anim");
		String direction = text.get("direction");
		String scroll = text.get("scroll");
		
		if(anim.equals("left")){
			setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_in_left));
		}else if(anim.equals("right")){
			setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_in_right));
		}
		
	}

	private String[] getValuesByLines(int w,String value,int lines) {
		String [] linestrs = getLineStrs(value, getPaint(), w);//getWidth() 0??
		System.out.println("linestrs.length-->"+linestrs.length + "  lines-->"+lines);
		String [] values = new String [(int) Math.ceil(linestrs.length/lines)+1];
		System.out.println("values.length->"+values.length);
		for(int i = 0;i<values.length;i++){
			values[i] = getValueFrom(linestrs,lines,i);
		}
		return values;
	}
	
	private String getValueFrom(String[] linestrs,int lines,int num){
		StringBuffer result = new StringBuffer();
		for(int i=num*lines;i<(num+1)*lines;i++){
			if(i>=linestrs.length)
				break;
			result.append(linestrs[i]);
		}
		return result.toString();	
	}
	
	
	private String[] getLineStrs(String content, Paint p, float width) { 
		System.out.println("width-->"+width);
        int length = content.length(); 
        float textWidth = p.measureText(content); 
        if(textWidth <= width) { 
            return new String[]{content}; 
        } 
        int lines = (int) Math.ceil(textWidth / width); //计算行数 //width
        int start = 0, end = length/lines, i = 0; 
        String[] lineTexts = new String[lines]; 
        while(start < length && end < length&&i<lines) {
           lineTexts[i++] = (String) content.subSequence(start, end); 
           start = end; 
           end += length/lines; 
        } 
        return lineTexts; 
    }
	
	
} 