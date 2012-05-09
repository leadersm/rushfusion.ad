package com.rushfusion.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	RelativeLayout adView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		adView = (RelativeLayout) findViewById(R.id.ad_show);
		Intent intent = getIntent();
		int testKind = intent.getIntExtra("kind", 0);
		AdCreator creator = new AdCreator(this,"", testKind, new AdCreator.CallBack() {
			
			@Override
			public void onError(Exception e, int errorCode) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
		});
		creator.startDefautModel();

	}

	
	


}