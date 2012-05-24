package com.rushfusion.ad;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * 1、内容过长时，按时间自动切换 2、切换时有动画
 * 
 * @author rushfusion.lsm
 */
public class AdText {

	private HashMap<String, String> text;
	private int pageCount;
	private int color = Color.WHITE;
	private int baseLine;
	private float textSize = 18;
	private int maxlines;
	private int w = 100;
	private int h = 300;
	private String[] linestrs;
	private Timer timer;
	private Paint mPaint = new Paint();
	private Context mContext;
	private ViewFlipper vf;
	private String[] strs;
	private static final int ChangeText = 202;
	private int mCurrentTextIndex = 0;
	private int animList[] = new int[] { R.anim.push_in_left,
			R.anim.push_out_left, R.anim.push_in_right, R.anim.push_out_right,
			R.anim.push_in_top, R.anim.push_out_top, R.anim.push_in_bottom,
			R.anim.push_out_bottom };

	private TimerTask tasktext = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = ChangeText;
			handler.sendMessage(message);
		}
	};
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ChangeText:
				setTextAnimation(vf, text.get("direction"), strs);
				break;
			}
		}
	};

	public AdText(Context context, ViewFlipper vf,HashMap<String, String> text,int w,int h,float textSize) {
		this.mContext = context;
		this.vf = vf;
		this.text = text;
		this.w = w;
		this.h = h;
		this.textSize = textSize;
		init(text);
	}

	public void start(){
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] != null){
				AdTextView at = new AdTextView(mContext);
				at.setText(getValueFrom(linestrs, maxlines, i));
				vf.addView(at);
			}
		}
		timer = new Timer();
		timer.schedule(tasktext, 5 * 1000, 5 * 1000);
	}
	private void init(HashMap<String, String> text) {
		mPaint.setAntiAlias(true);
		mPaint.setColor(color);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, textSize));
		baseLine = (int) Math.ceil((mPaint.getFontMetrics().descent
						- mPaint.getFontMetrics().ascent + mPaint
						.getFontMetrics().leading));
		maxlines = (int) Math.floor((float) h / baseLine);
//		System.out.println("h-->"+h+"-baseLine->"+baseLine+"-maxlines->"+maxlines);
		
		String value = text.get("value");
		Pattern p = Pattern.compile("\\t|\r|\n");
		Matcher m = p.matcher(value);
		value = m.replaceAll("").trim();
		
		linestrs = getLineStrs(text.get("value"), mPaint, w);
		pageCount = (int) Math.ceil((float) linestrs.length / maxlines);
		strs = getValuesByLines(w, value, maxlines, mPaint);
	}

	private class AdTextView extends View {
		String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public AdTextView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int lineCount = 0;
			if (text == null)
				return;
			char[] textCharArray = text.toCharArray();
			float drawedWidth = 0;
			float charWidth;
			for (int i = 0; i < textCharArray.length; i++) {
				charWidth = mPaint.measureText(textCharArray, i, 1);
				if (textCharArray[i] == '\n') {
					lineCount++;
					drawedWidth = 0;
					continue;
				}
				if (w - drawedWidth < charWidth) {
					lineCount++;
					drawedWidth = 0;
				}
				canvas.drawText(textCharArray, i, 1, drawedWidth,(lineCount + 1) * baseLine, mPaint);
				drawedWidth += charWidth;
			}
		}

	}

	public float getRawSize(int unit, float size) {
		Context c = mContext;
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}

	private String[] getValuesByLines(int w, String value, int maxlines,
			Paint paint) {
		String[] values = new String[pageCount];
//		System.out.println("累计行数-->" + linestrs.length + "  最大行数-->" + maxlines
//				+ "  页数-->" + values.length);
		for (int i = 0; i < values.length; i++) {
			values[i] = getValueFrom(linestrs, maxlines, i);
//			System.out.println("value-->" + values[i]);
		}
		return values;
	}

	private String getValueFrom(String[] linestrs, int maxlines, int num) {
		StringBuffer result = new StringBuffer();
		for (int i = num * maxlines; i < (num + 1) * maxlines; i++) {
			if (i >= linestrs.length)
				break;
			if (linestrs[i] != null)
				result.append(linestrs[i]);
		}
		return result.toString();
	}

	private String[] getLineStrs(String content, Paint p, float width) {
		int lineCount = 0;
		if (content == null)
			return null;
		char[] textCharArray = content.toCharArray();
		float drawedWidth = 0;
		float charWidth;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = mPaint.measureText(textCharArray, i, 1);

			if (textCharArray[i] == '\n') {
				lineCount++;
				drawedWidth = 0;
				continue;
			}
			if (w - drawedWidth < charWidth) {
				lineCount++;
				drawedWidth = 0;
			}
			if (i == textCharArray.length - 1) {
				lineCount++;
				drawedWidth = 0;
			}
			drawedWidth += charWidth;
		}
		
		int start = 0;
		int end = 0;
		String[] result = new String[lineCount + 1];
		for (int i = 0, j = 0; i < textCharArray.length && j < lineCount + 1; i++) {
			charWidth = mPaint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				end = i;
				result[j] = content.subSequence(start, end).toString();
//				System.out.println("1 result-" + j + "-->" + result[j]);
				start = end;
				j++;
				drawedWidth = 0;
				continue;
			}
			if (w - drawedWidth < charWidth) {
				end = i;
				result[j] = content.subSequence(start, end).toString();
//				System.out.println("2 result-" + j + "-->" + result[j]);
				start = end;
				j++;
				drawedWidth = 0;
			}
			if (i == textCharArray.length - 1) {
				end = i;
				result[j] = content.subSequence(start, end).toString();
//				System.out.println("3 result-" + j + "-->" + result[j]);
				start = end;
				j++;
			}
			drawedWidth += charWidth;
		}
		return result;
	}
	private void setTextAnimation(final ViewFlipper vf, String direction,
			String[] strs) {
		vf.clearAnimation();
		mCurrentTextIndex = mCurrentTextIndex % (strs.length);
		if ("left".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[0]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[1]));
		} else if ("right".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[2]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[3]));
		} else if ("top".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[4]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[5]));
		} else if ("bottom".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[6]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[7]));
		}
		vf.showNext();
		mCurrentTextIndex++;
	}
}