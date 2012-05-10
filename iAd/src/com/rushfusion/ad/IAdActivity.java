package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		AdCreator creator = new AdCreator(this,"",new AdCreator.CallBack() {
			
			@Override
			public void onError(Exception e, int errorCode) {
				// TODO Auto-generated method stub
				System.out.println(e.getMessage());
			}
		});
//		creator.setAdSize(300,400);
		creator.setAdSize(200,LayoutParams.WRAP_CONTENT);
		creator.start();

	}

	
	


}