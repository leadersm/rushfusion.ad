package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestActivity extends Activity {
	
	Activity mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(new TextView(this));
        ViewGroup mRoot = (ViewGroup) getWindow().getDecorView();
        AdPage ad = new AdPage();
        ad.init(mContext, mRoot,null);
        ad.run();			
    }
    
   
}