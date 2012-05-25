package com.rushfusion.ad;

import org.w3c.dom.Node;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

public abstract class  BasePage implements Runnable{
	protected Context mContext;
	protected ViewGroup mContainer;
	protected Node mParams;
//	protected View mContentView;
	
	public BasePage() {
		
	}
	
	/**
	 * get the contentView 
	 * @return contentView
	 */

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
