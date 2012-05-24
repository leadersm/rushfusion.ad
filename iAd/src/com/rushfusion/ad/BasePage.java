package com.rushfusion.ad;

import org.w3c.dom.Node;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public abstract class  BasePage implements Runnable,View.OnKeyListener{
	protected Context mContext;
	protected ViewGroup mContainer;
	protected Node mParams;
	protected View mContentView;
	
	public BasePage() {
		
	}
	
	/**
	 * create the contentView that you want to attach to mContainer
	 * @param resId the layout's id
	 * @return the contentView
	 */
	public View onCreateContentView(int resId) {
		// TODO Auto-generated method stub
		mContentView = View.inflate(mContext, resId, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
		mContainer.removeAllViews();
		mContainer.addView(mContentView, lp);
		System.out.println("count"+mContainer.getChildCount());
		for(int i=0;i<mContainer.getChildCount();i++){
			mContainer.getChildAt(i).setFocusable(true);
			mContainer.getChildAt(i).setOnKeyListener(this);
		}
		mContentView.requestFocus();
		return mContentView;
	}


	/**
	 * get the contentView 
	 * @return contentView
	 */
	public View getContentView() {
		return mContentView;
	}

	/**
	 * @param context, the application context
	 * @param container, the panel of your app need to attach to
	 * @param params ,if you need please provide,else null
	 */
	public void init(Context context ,ViewGroup container,Node params) {
		mContext = context;
		mContainer = container;
		mParams = params;
		onParseXml(params);
	}

	public abstract void onParseXml(Node params);
	
	
	public Dialog createDialog(int resId){
		Dialog dialog = new Dialog(mContext);
		dialog.setContentView(resId);
		dialog.setOwnerActivity((Activity)mContext);
		return dialog;
	}
	
}
