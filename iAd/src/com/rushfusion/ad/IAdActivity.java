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
}
	
	
	Runnable r1 = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator1 = AdCreator.getInstance(IAdActivity.this, "", new AdCreator.CallBack() {
						
						@Override
						public void onError(Exception e,
								int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator1.TEST_XML = "data.xml";
//			creator1.setAdSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			creator1.start();
		}
	};

}
