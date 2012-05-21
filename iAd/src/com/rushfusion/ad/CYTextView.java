package com.rushfusion.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CYTextView extends TextView{
	private final String namespace = "http://www.angellecho.com/";
	 public  static  int m_iTextHeight; //文本的高度
	private String text;
	private float textSize;
	private float paddingLeft=0;
	private float paddingRight=0;
	private float marginLeft=0;
	private float marginRight=0;
	private int textColor;
	private Context context;
	
	private Paint paint1 = new Paint();
	private float textShowWidth;

	 private float LineSpace = 0;//行间距
	  public  static  int m_iTextWidth;//文本的宽度
	public CYTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		text = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/android", "text");
		textSize = attrs.getAttributeIntValue(namespace, "textSize", 15);
		textColor = attrs.getAttributeIntValue(namespace, "textColor",Color.WHITE);
		paddingLeft = attrs.getAttributeIntValue(namespace, "paddingLeft", 0);
		paddingRight = attrs.getAttributeIntValue(namespace, "paddingRight", 0);
		marginLeft = attrs.getAttributeIntValue(namespace, "marginLeft", 0);
		marginRight = attrs.getAttributeIntValue(namespace, "marginRight", 0);
		m_iTextWidth=320;
        LineSpace=15;
		paint1.setTextSize(textSize);
		paint1.setColor(textColor);
		paint1.setAntiAlias(true);
//		textShowWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth()
//				- context.get
//				- paddingLeft - paddingRight - marginLeft - marginRight;
		Singleton s = Singleton.getInstance();
		int textsize2 = s.getTextsize();
		textShowWidth = textsize2;
	}
	@Override
	protected void onDraw(Canvas canvas) {
	//	super.onDraw(canvas);
		int lineCount = 0;
		text = this.getText().toString();//.replaceAll("\n", "\r\n");
		if(text==null)return;
		char[] textCharArray = text.toCharArray();
		// �ѻ�Ŀ��
		float drawedWidth = 0;
		float charWidth;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = paint1.measureText(textCharArray, i, 1);
			
			if(textCharArray[i]=='\n'){
				lineCount++;
				drawedWidth = 0;
				continue;
			}
			if (textShowWidth - drawedWidth < charWidth) {
				lineCount++;
				drawedWidth = 0;
			}
			canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth,
					(lineCount + 1) * textSize, paint1);
			drawedWidth += charWidth;
			
			Log.d("testMy", "drawedWidth is:" +drawedWidth);
		}
		setHeight((lineCount + 1) * (int) textSize + 5);
	}
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	    {         
	        int measuredHeight = measureHeight(heightMeasureSpec);         
	        int measuredWidth = measureWidth(widthMeasureSpec);  
	        System.out.println("measuredHeight========>"+measuredHeight);
	        System.out.println("measuredWidth========>"+measuredWidth);
	        this.setMeasuredDimension(measuredWidth, measuredHeight);
	        this.setLayoutParams(new LinearLayout.LayoutParams(measuredWidth,measuredHeight));
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    } 
	                
	    private int measureHeight(int measureSpec)
	    { 
	        int specMode = MeasureSpec.getMode(measureSpec);         
	        int specSize = MeasureSpec.getSize(measureSpec);                  
	        // Default size if no limits are specified. 
	      int result = m_iTextHeight;         
	        if (specMode == MeasureSpec.AT_MOST){        
	            // Calculate the ideal size of your         
	            // control within this maximum size.         
	            // If your control fills the available          
	            // space return the outer bound.         
	            result = specSize;          
	        }else if (specMode == MeasureSpec.EXACTLY){          
	            // If your control can fit within these bounds return that value.           
	            result = specSize;          
	        }          
	        return result;           
	    } 
	   
	    private int measureWidth(int measureSpec)
	    { 
	        int specMode = MeasureSpec.getMode(measureSpec);          
	        int specSize = MeasureSpec.getSize(measureSpec);            
	         
	        // Default size if no limits are specified.         
	        int result = 500;         
	        if (specMode == MeasureSpec.AT_MOST){         
	            // Calculate the ideal size of your control          
	            // within this maximum size.        
	            // If your control fills the available space        
	            // return the outer bound.        
	            result = specSize;         
	        }else if (specMode == MeasureSpec.EXACTLY){          
	            // If your control can fit within these bounds return that value.          
	            result = specSize;           
	        }          
	        return result;         
	    }
	   
	  
	 

}
