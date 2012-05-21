package com.rushfusion.ad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class IAdActivity extends Activity {
	/** Called when the activity is first created. */
	
	boolean isShowing1=false;
	boolean isShowing2=false;
	boolean isShowing3=false;
	boolean isShowing4=false;
	AdCreator creator1;
	AdCreator creator2;
	AdCreator creator3;
	AdCreator creator4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		
		final Button start1 = (Button) findViewById(R.id.button1);
		final Button start2 = (Button) findViewById(R.id.button2);
		final Button start3 = (Button) findViewById(R.id.button3);
		final Button start4 = (Button) findViewById(R.id.button4);

		
		start1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isShowing1){
					isShowing1 = true;
					start1.post(r1);
				}else{
					isShowing1 = false;
					creator1.stop();
				}
			}
		});
		start2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isShowing2){
					isShowing2 = true;
					start2.post(r2);
				}else{
					isShowing2 = false;
					creator2.stop();
				}
			}
		});
		start3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isShowing3){
					isShowing3 = true;
					start3.post(r3);
				}else{
					isShowing3 = false;
					creator3.stop();
				}
			}
		});
		start4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isShowing4){
					isShowing4 = true;
					start4.post(r4);
				}else{
					isShowing4 = false;
					creator4.stop();
				}
				
			}
		});
		
}
	
	
	Runnable r1 = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator1 = new AdCreator(IAdActivity.this, "", new AdCreator.CallBack() {
						
						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator1.TEST_XML = "data1.xml";
			creator1.setAdSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			creator1.start();
		}
	};
	Runnable r2 = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator2 = new AdCreator(IAdActivity.this, "", new AdCreator.CallBack() {

						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator2.TEST_XML = "data2.xml";
			creator2.setAdSize(500, 100);
			creator2.start();
		}
	};
	Runnable r3 = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator3 = new AdCreator(IAdActivity.this, "", new AdCreator.CallBack() {

						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator3.TEST_XML = "data3.xml";
			creator3.setAdSize(300, 300);
			creator3.start();
		}
	};

	Runnable r4 = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator4 = new AdCreator(IAdActivity.this, "", new AdCreator.CallBack() {

						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator4.TEST_XML = "data4.xml";
			creator4.setAdSize(300, 300);
			creator4.start();
		}
	};

}
