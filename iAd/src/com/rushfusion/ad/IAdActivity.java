package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		TextView tv = new TextView(this);
		tv.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AdCreator creator1 = new AdCreator(IAdActivity.this,"",new AdCreator.CallBack() {
					
					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
				creator1.TEST_XML = "data1.xml";
				creator1.setAdSize(300,300);
				creator1.start();
			}
		});
		
		tv.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AdCreator creator4 = new AdCreator(IAdActivity.this,"",new AdCreator.CallBack() {
					
					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
				creator4.TEST_XML = "data4.xml";
				creator4.setAdSize(300,300);
				creator4.start();
				
			}
		});
		tv.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AdCreator creator2 = new AdCreator(IAdActivity.this,"",new AdCreator.CallBack() {
					
					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
				creator2.TEST_XML = "data2.xml";
				creator2.setAdSize(300,300);
				creator2.start();
			}
		});
		tv.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				AdCreator creator3 = new AdCreator(IAdActivity.this,"",new AdCreator.CallBack() {
					
					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
				creator3.TEST_XML = "data3.xml";
				creator3.setAdSize(300,300);
				creator3.start();
			}
		});
		
		

	}

	
	


}